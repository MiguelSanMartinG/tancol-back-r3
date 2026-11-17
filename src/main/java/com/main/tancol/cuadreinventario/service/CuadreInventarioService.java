package com.main.tancol.cuadreinventario.service;

import com.main.tancol.almacen.entities.AlmacenEntity;
import com.main.tancol.almacen.entities.InventarioEntity;
import com.main.tancol.almacen.repositories.AlmacenRepository;
import com.main.tancol.almacen.repositories.InventarioRepository;
import com.main.tancol.common.exceptions.exeption.GenericExeption;
import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.cuadreinventario.dto.CuadreInventarioDTOs.*;
import com.main.tancol.cuadreinventario.entities.CuadreInventarioEntity;
import com.main.tancol.cuadreinventario.entities.CuadreInventarioPartidaEntity;
import com.main.tancol.cuadreinventario.enums.EstadoCuadre;
import com.main.tancol.cuadreinventario.repositories.CuadreInventarioRepository;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.empleados.helpers.EmpleadoContext;
import com.main.tancol.producto.service.insumo.StockMinimoRevisionService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Cuadre de inventario por almacén. Al abrirlo se congela la existencia del sistema de cada
 * producto; se captura la cantidad física y al cerrarlo (con firma) el usuario decide si la
 * existencia se ajusta a lo contado o si el cuadre solo queda como registro. Cerrado no se modifica.
 */
@Service
public class CuadreInventarioService {

    static final ZoneId ZONA_MEXICO = ZoneId.of("America/Mexico_City");

    private final CuadreInventarioRepository repository;
    private final AlmacenRepository almacenRepository;
    private final InventarioRepository inventarioRepository;
    private final StockMinimoRevisionService stockMinimoRevisionService;

    public CuadreInventarioService(CuadreInventarioRepository repository,
                                   AlmacenRepository almacenRepository,
                                   InventarioRepository inventarioRepository,
                                   StockMinimoRevisionService stockMinimoRevisionService) {
        this.repository = repository;
        this.almacenRepository = almacenRepository;
        this.inventarioRepository = inventarioRepository;
        this.stockMinimoRevisionService = stockMinimoRevisionService;
    }

    @Transactional(readOnly = true)
    public List<Response> listar(UUID idAlmacen, EstadoCuadre estado) {
        Specification<CuadreInventarioEntity> spec = (root, query, cb) -> {
            List<Predicate> predicados = new ArrayList<>();
            predicados.add(cb.isFalse(root.get("deleted")));
            if (idAlmacen != null) {
                predicados.add(cb.equal(root.get("almacen").get("id"), idAlmacen));
            }
            if (estado != null) {
                predicados.add(cb.equal(root.get("estado"), estado));
            }
            return cb.and(predicados.toArray(new Predicate[0]));
        };
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "fechaCreacion")).stream()
                .map(c -> toResponse(c, false))
                .toList();
    }

    @Transactional(readOnly = true)
    public Response detalle(UUID id) {
        return toResponse(buscar(id), true);
    }

    @Transactional
    public Response crear(CrearRequest dto) {
        AlmacenEntity almacen = almacenRepository.findById(dto.getIdAlmacen())
                .filter(a -> !a.isDeleted())
                .orElseThrow(() -> new IDNotFound("No se encontró el almacén"));
        if (repository.existsByAlmacen_IdAndEstadoAndDeletedFalse(almacen.getId(), EstadoCuadre.ABIERTO)) {
            throw new GenericExeption("Ya hay un cuadre abierto para el almacén " + almacen.getNombre()
                    + ". Ciérralo o cancélalo antes de abrir otro.");
        }
        List<InventarioEntity> inventario = inventarioRepository.findAll(
                (root, query, cb) -> cb.and(
                        cb.equal(root.get("almacen").get("id"), almacen.getId()),
                        cb.isFalse(root.get("deleted"))));
        if (inventario.isEmpty()) {
            throw new GenericExeption("El almacén " + almacen.getNombre() + " no tiene inventario para cuadrar.");
        }

        CuadreInventarioEntity cuadre = CuadreInventarioEntity.builder()
                .folio(String.format("CI-%05d", repository.siguienteFolio()))
                .almacen(almacen)
                .estado(EstadoCuadre.ABIERTO)
                .creadoPor(EmpleadoContext.getEmpleadoActual())
                .fechaCreacion(ahora())
                .ajusteAplicado(false)
                .build();
        for (InventarioEntity item : inventario) {
            var producto = item.getProductoBasicoEntity();
            cuadre.getPartidas().add(CuadreInventarioPartidaEntity.builder()
                    .cuadre(cuadre)
                    .inventario(item)
                    .productoId(producto != null ? producto.getId() : null)
                    .producto(producto != null ? producto.getNombre() : "Sin producto")
                    .sku(producto != null ? producto.getSku() : null)
                    .unidad(item.getUnidadDeMedida() != null ? item.getUnidadDeMedida().getNombre() : null)
                    .cantidadSistema(item.getCantidad() != null ? item.getCantidad() : BigDecimal.ZERO)
                    .build());
        }
        cuadre.getPartidas().sort(Comparator.comparing(
                p -> Optional.ofNullable(p.getProducto()).orElse(""), String.CASE_INSENSITIVE_ORDER));
        return toResponse(repository.save(cuadre), true);
    }

    @Transactional
    public Response capturar(UUID id, CapturaRequest dto) {
        CuadreInventarioEntity cuadre = buscarAbierto(id);
        aplicarCaptura(cuadre, dto.getPartidas(), dto.getObservaciones());
        return toResponse(repository.save(cuadre), true);
    }

    @Transactional
    public Response cerrar(UUID id, CerrarRequest dto) {
        CuadreInventarioEntity cuadre = buscarAbierto(id);
        if (dto.getIdImagenFirma() == null || dto.getIdImagenFirma().isBlank()) {
            throw new GenericExeption("La firma es obligatoria para cerrar el cuadre.");
        }
        if (dto.getObservaciones() != null) {
            cuadre.setObservaciones(limpiar(dto.getObservaciones()));
        }
        long faltantes = cuadre.getPartidas().stream().filter(p -> p.getCantidadFisica() == null).count();
        if (faltantes > 0) {
            throw new GenericExeption("Falta capturar la cantidad actual de " + faltantes
                    + (faltantes == 1 ? " producto." : " productos."));
        }

        boolean ajustar = Boolean.TRUE.equals(dto.getAjustarExistencia());
        if (ajustar) {
            ajustarExistencia(cuadre);
        }
        LocalDateTime cierre = ahora();
        cuadre.setEstado(EstadoCuadre.CERRADO);
        cuadre.setAjusteAplicado(ajustar);
        cuadre.setCerradoPor(EmpleadoContext.getEmpleadoActual());
        cuadre.setFechaCierre(cierre);
        cuadre.setIdImagenFirma(dto.getIdImagenFirma().trim());
        cuadre.setFechaFirma(cierre);
        return toResponse(repository.save(cuadre), true);
    }

    @Transactional
    public Response cancelar(UUID id) {
        CuadreInventarioEntity cuadre = buscarAbierto(id);
        cuadre.setDeleted(true);
        return toResponse(repository.save(cuadre), false);
    }

    /** La existencia queda igual a lo contado; si bajó, se revisa el stock mínimo como en una salida. */
    private void ajustarExistencia(CuadreInventarioEntity cuadre) {
        Set<UUID> productosQueBajaron = new LinkedHashSet<>();
        for (CuadreInventarioPartidaEntity partida : cuadre.getPartidas()) {
            InventarioEntity item = partida.getInventario();
            if (item == null || item.isDeleted()) {
                continue;
            }
            BigDecimal actual = item.getCantidad() != null ? item.getCantidad() : BigDecimal.ZERO;
            if (actual.compareTo(partida.getCantidadFisica()) == 0) {
                continue;
            }
            if (partida.getCantidadFisica().compareTo(actual) < 0 && partida.getProductoId() != null) {
                productosQueBajaron.add(partida.getProductoId());
            }
            item.setCantidad(partida.getCantidadFisica());
            inventarioRepository.save(item);
        }
        productosQueBajaron.forEach(stockMinimoRevisionService::revisarTrasSalida);
    }

    static void aplicarCaptura(CuadreInventarioEntity cuadre, List<CapturaPartida> capturas, String observaciones) {
        Map<UUID, CuadreInventarioPartidaEntity> porId = cuadre.getPartidas().stream()
                .collect(Collectors.toMap(CuadreInventarioPartidaEntity::getId, p -> p));
        for (CapturaPartida captura : Optional.ofNullable(capturas).orElse(List.of())) {
            CuadreInventarioPartidaEntity partida = porId.get(captura.getIdPartida());
            if (partida == null) {
                throw new GenericExeption("La partida no pertenece a este cuadre.");
            }
            BigDecimal cantidad = captura.getCantidadFisica();
            if (cantidad != null && cantidad.signum() < 0) {
                throw new GenericExeption("La cantidad actual no puede ser negativa.");
            }
            partida.setCantidadFisica(cantidad);
        }
        cuadre.setObservaciones(limpiar(observaciones));
    }

    private CuadreInventarioEntity buscar(UUID id) {
        return repository.findById(id)
                .filter(c -> !c.isDeleted())
                .orElseThrow(() -> new IDNotFound("No se encontró el cuadre de inventario"));
    }

    private CuadreInventarioEntity buscarAbierto(UUID id) {
        CuadreInventarioEntity cuadre = buscar(id);
        if (cuadre.getEstado() != EstadoCuadre.ABIERTO) {
            throw new GenericExeption("El cuadre ya está cerrado y no se puede modificar.");
        }
        return cuadre;
    }

    private static LocalDateTime ahora() {
        return LocalDateTime.now(ZONA_MEXICO);
    }

    private static String limpiar(String valor) {
        if (valor == null) {
            return null;
        }
        String recortado = valor.trim();
        return recortado.isEmpty() ? null : recortado;
    }

    static String nombre(EmpleadosEntity e) {
        if (e == null) {
            return null;
        }
        String completo = Stream.of(e.getNombre(), e.getPrimerApellido(), e.getSegundoApellido())
                .filter(parte -> parte != null && !parte.isBlank())
                .map(String::trim)
                .collect(Collectors.joining(" "));
        return completo.isEmpty() ? e.getNombreUsuario() : completo;
    }

    static Response toResponse(CuadreInventarioEntity c, boolean conDetalle) {
        List<CuadreInventarioPartidaEntity> partidas = c.getPartidas();
        int capturadas = (int) partidas.stream().filter(p -> p.getCantidadFisica() != null).count();
        int conDiferencia = (int) partidas.stream()
                .filter(p -> p.getDiferencia() != null && p.getDiferencia().signum() != 0)
                .count();
        return Response.builder()
                .id(c.getId())
                .folio(c.getFolio())
                .almacen(new AlmacenRef(c.getAlmacen().getId(), c.getAlmacen().getNombre()))
                .estado(c.getEstado())
                .creadoPor(nombre(c.getCreadoPor()))
                .fechaCreacion(c.getFechaCreacion())
                .cerradoPor(nombre(c.getCerradoPor()))
                .fechaCierre(c.getFechaCierre())
                .ajusteAplicado(c.isAjusteAplicado())
                .totalPartidas(partidas.size())
                .partidasCapturadas(capturadas)
                .partidasConDiferencia(conDiferencia)
                .observaciones(conDetalle ? c.getObservaciones() : null)
                .idImagenFirma(conDetalle ? c.getIdImagenFirma() : null)
                .fechaFirma(conDetalle ? c.getFechaFirma() : null)
                .partidas(conDetalle ? partidas.stream()
                        .map(p -> PartidaResponse.builder()
                                .idPartida(p.getId())
                                .idProducto(p.getProductoId())
                                .producto(p.getProducto())
                                .sku(p.getSku())
                                .unidad(p.getUnidad())
                                .cantidadSistema(p.getCantidadSistema())
                                .cantidadFisica(p.getCantidadFisica())
                                .diferencia(p.getDiferencia())
                                .build())
                        .toList() : null)
                .build();
    }
}
