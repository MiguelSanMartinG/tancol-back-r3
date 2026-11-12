package com.main.tancol.orden.service;

import com.main.tancol.almacen.entities.Almacen;
import com.main.tancol.almacen.repositories.AlmacenRepository;
import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.orden.dto.request.OrdenFacturacionRequestDTO;
import com.main.tancol.orden.dto.request.RecepcionOrdenRequestDTO;
import com.main.tancol.orden.dto.request.SolicitudOrdenRequestDTO;
import com.main.tancol.orden.dto.request.PartidaOrdenRequestDTO;
import com.main.tancol.orden.dto.response.OrdenFacturacionResponseDTO;
import com.main.tancol.orden.dto.response.RecepcionOrdenResponseDTO;
import com.main.tancol.orden.dto.response.SolicitudOrdenResponseDTO;
import com.main.tancol.orden.entities.OrdenEntity;
import com.main.tancol.orden.entities.PartidaOrdenEntity;
import com.main.tancol.orden.enums.FacturacionOrdenEstatus;
import com.main.tancol.orden.enums.Prioridad;
import com.main.tancol.orden.enums.RecepcionEstatus;
import com.main.tancol.orden.enums.SolicitanteEstatus;
import com.main.tancol.orden.repositories.OrdenRepository;
import com.main.tancol.orden.repositories.PartidaOrdenRepository;
import com.main.tancol.orden.service.interfaces.IOrdenService;
import com.main.tancol.orden.util.OrdenSpecification;
import com.main.tancol.orden.util.OrdenValueMapper;
import com.main.tancol.orden.util.PartidaOrdenValueMapper;
import com.main.tancol.producto.entities.ProductoEntity;
import com.main.tancol.producto.repositories.ProductoRepository;
import com.main.tancol.proveedor.entity.ProveedorEntity;
import com.main.tancol.proveedor.entity.TipoImpuesto;
import com.main.tancol.proveedor.repositories.ImpuestosRepository;
import com.main.tancol.proveedor.repositories.ProveedorRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrdenService implements IOrdenService {

    private final OrdenRepository repository;
    private final PartidaOrdenRepository partidaOrdenRepository;
    private final ProveedorRepository proveedorRepository;
    private final ImpuestosRepository impuestosRepository;
    private final ProductoRepository productoRepository;
    private final AlmacenRepository almacenRepository;
    private final JdbcTemplate jdbcTemplate;


    public OrdenService(OrdenRepository repository, PartidaOrdenRepository partidaOrdenRepository, ProveedorRepository proveedorRepository, PartidaOrdenService partidaOrdenService, ImpuestosRepository impuestosRepository, ProductoRepository productoRepository, AlmacenRepository almacenRepository, JdbcTemplate jdbcTemplate) {
        this.repository = repository;
        this.partidaOrdenRepository = partidaOrdenRepository;
        this.proveedorRepository = proveedorRepository;

        this.impuestosRepository = impuestosRepository;
        this.productoRepository = productoRepository;
        this.almacenRepository = almacenRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    private static BigDecimal calcularTotalPartida(PartidaOrdenRequestDTO row, TipoImpuesto t) {
        // TOTAL = (PU * CANTIDAD) + ((PU*CAN)*IMPUESTO/100) - DESCUENTO
        BigDecimal pu = row.getPrecioUnitario();
        BigDecimal cantidad = BigDecimal.valueOf(row.getCantidad());
        BigDecimal impuesto = BigDecimal.valueOf(t.getPorcentaje()/100.00);
        BigDecimal descuento = row.getDescuento();

        log.info("PU, CANTIDAD, IMPUESTO: "+ pu.toString() + " " + cantidad.toString() + " " + impuesto.toString());

        BigDecimal precioPorCantidad = pu.multiply(cantidad);
        log.info("Precio por cantidad:" + precioPorCantidad.toString());

        BigDecimal totalPartida = (precioPorCantidad.add(precioPorCantidad.multiply(impuesto)));
        BigDecimal totalConDescuento = totalPartida.subtract(descuento);
        log.info(String.valueOf(totalConDescuento));
        return totalConDescuento;
    }

    @Override
    @Transactional
    public SolicitudOrdenResponseDTO create(SolicitudOrdenRequestDTO dto) {

        ProveedorEntity queryProveedorEntity = proveedorRepository.findById(dto.getProveedorId())
                .orElseThrow(() -> new IDNotFound("La id del proveedor no existe"));
        String folio = generarFolio();
        OrdenEntity newOrden = OrdenValueMapper.OrdenRequestDTOToEntity(dto, folio, queryProveedorEntity);
        OrdenEntity createdOrden = this.repository.save(newOrden);

        AtomicReference<BigDecimal> acumuladorTotalOrden = new AtomicReference<>(new BigDecimal("0.0"));
        List<PartidaOrdenEntity> partidas = dto.getPartidas().stream()
                .map(row -> {
                    // Buscar el tipo de impuesto y el producto
                    TipoImpuesto t = this.impuestosRepository.findById(row.getIdTipoImpuesto())
                            .orElseThrow(() -> new IDNotFound("Id no encontrada del impuesto"));
                    ProductoEntity p = this.productoRepository.findById(row.getIdProducto())
                            .orElseThrow(() -> new IDNotFound("Id no encontrada del producto"));



                    BigDecimal totalPartida = calcularTotalPartida(row, t);
                    acumuladorTotalOrden.updateAndGet(currentTotal -> currentTotal.add(totalPartida));
                    // Crear la partida y asociarla a la orden
                    return PartidaOrdenValueMapper.PartidaOrdenRequestToEntity(row, createdOrden, t, p);
                })
                .collect(Collectors.toList()); // Recolectar las partidas en una lista

        createdOrden.setPartidas(partidas);
        createdOrden.setTotalOrden(acumuladorTotalOrden.get());

        List<PartidaOrdenEntity> partidasGuardadas = (List<PartidaOrdenEntity>) this.partidaOrdenRepository.saveAll(partidas);
        OrdenEntity orden = repository.save(createdOrden);

        return OrdenValueMapper.OrdenEntityToResponseDTO(orden, partidasGuardadas);
    }

    @Override
    @Transactional
    public SolicitudOrdenResponseDTO update(UUID id, SolicitudOrdenRequestDTO dto) {
        OrdenEntity query = findById(id);
        ProveedorEntity queryProveedorEntity = proveedorRepository.findById(dto.getProveedorId()).orElseThrow(() -> new IDNotFound("La id del proveedor no existe"));
        OrdenEntity updatedOrder = OrdenValueMapper.OrdenRequestDTOToEntity(dto, query.getFolio(), queryProveedorEntity);
        updatedOrder.setId(query.getId());

        AtomicReference<BigDecimal> acumuladorTotalOrden = new AtomicReference<>(new BigDecimal("0.0"));
        List<PartidaOrdenEntity> partidas = dto.getPartidas().stream()
                .map((row) -> {
                    // Buscar el tipo de impuesto y el producto
                    TipoImpuesto t = this.impuestosRepository.findById(row.getIdTipoImpuesto())
                            .orElseThrow(() -> new IDNotFound("Id no encontrada del impuesto: " + row.getIdTipoImpuesto()));
                    ProductoEntity p = this.productoRepository.findById(row.getIdProducto())
                            .orElseThrow(() -> new IDNotFound("Id no encontrada del producto: " + row.getIdProducto()));

                    BigDecimal totalPartida = calcularTotalPartida(row, t);
                    acumuladorTotalOrden.updateAndGet(currentTotal -> currentTotal.add(totalPartida));
                    // Crear la partida y asociarla a la orden
                    return PartidaOrdenValueMapper.PartidaOrdenRequestToEntity(row, updatedOrder, t, p);
                })
                .collect(Collectors.toList()); // Recolectar las partidas en una lista

        updatedOrder.getPartidas().clear();
        updatedOrder.getPartidas().addAll(partidas);
        updatedOrder.setTotalOrden(acumuladorTotalOrden.get());
        updatedOrder.setUpdatedAt(LocalDateTime.now());

        OrdenEntity dbOrden = this.repository.save(updatedOrder);

        return OrdenValueMapper.OrdenEntityToResponseDTO(dbOrden, dbOrden.getPartidas());
    }

    @Override
    public SolicitudOrdenResponseDTO delete(UUID id) {
        OrdenEntity query = findById(id);
        query.setDeleted(true);
        query = repository.save(query);
        return OrdenValueMapper.OrdenEntityToResponseDTO(query, query.getPartidas());
    }

    @Override
    public SolicitudOrdenResponseDTO getById(UUID id) {
        OrdenEntity query = findById(id);
        return OrdenValueMapper.OrdenEntityToResponseDTO(query, query.getPartidas());
    }
    public OrdenFacturacionResponseDTO getByIdFactura(UUID id) {
        OrdenEntity query = findById(id);
        return OrdenValueMapper.OrdenFacturacionEntityToResponseDTO(query);
    }

    public RecepcionOrdenResponseDTO getByIdRecibo(UUID id) {
        OrdenEntity query = findById(id);
        return OrdenValueMapper.OrdenEntityToRecepcionOrdenResponseDTO(query);
    }

    @Override
    public List<SolicitudOrdenResponseDTO> getAll(Map<String, String> params) {
        List<OrdenEntity> list = repository.findAll(filterWithParameters(params));
        return list.stream().map((x) -> OrdenValueMapper.OrdenEntityToResponseDTO(x, x.getPartidas())).toList();
    }

    public List<OrdenFacturacionResponseDTO> getAllFacturacion() {
        List<OrdenEntity> list = repository.findByPostAutorizadoEstatusNotNull();
        return list.stream().map(OrdenValueMapper::OrdenFacturacionEntityToResponseDTO).toList();
    }

    @Transactional
    public OrdenFacturacionResponseDTO updateFacturacion(OrdenFacturacionRequestDTO dto) {
        OrdenEntity query = findById(dto.getOrderId());

        // Si es null, o Cancelado, o Completado o Facturado no se puede editar
        if(query.getPostAutorizadoEstatus() != FacturacionOrdenEstatus.BORRADOR && query.getPostAutorizadoEstatus() != FacturacionOrdenEstatus.PENDIENTE){
            throw new RuntimeException("El estado no es valido para editarlo");
        }

        // Si fue facturado guarda la hora.
        if(dto.getFacturacionOrdenEstatus().equals(FacturacionOrdenEstatus.FACTURADO)){
            query.setFechaFacturacion(LocalDateTime.now());
        }

        query.setPostAutorizadoEstatus(dto.getFacturacionOrdenEstatus());
        query.setFolioExterno(dto.getFolioExterno());
        query.setComentarioFactura(dto.getComentarioFactura());


        AtomicReference<BigDecimal> acumuladorTotalOrden = new AtomicReference<>(new BigDecimal("0.0"));
        List<PartidaOrdenEntity> partidas = dto.getPartidas().stream()
                .map((row) -> {
                    // Buscar el tipo de impuesto y el producto
                    TipoImpuesto t = this.impuestosRepository.findById(row.getIdTipoImpuesto())
                            .orElseThrow(() -> new IDNotFound("Id no encontrada del impuesto: " + row.getIdTipoImpuesto()));
                    ProductoEntity p = this.productoRepository.findById(row.getIdProducto())
                            .orElseThrow(() -> new IDNotFound("Id no encontrada del producto: " + row.getIdProducto()));

                    BigDecimal totalPartida = calcularTotalPartida(row, t);
                    acumuladorTotalOrden.updateAndGet(currentTotal -> currentTotal.add(totalPartida));
                    // Crear la partida y asociarla a la orden
                    return PartidaOrdenValueMapper.PartidaOrdenRequestToEntity(row, query, t, p);
                })
                .collect(Collectors.toList()); // Recolectar las partidas en una lista

        query.getPartidas().clear();
        query.getPartidas().addAll(partidas);
        query.setTotalOrden(acumuladorTotalOrden.get());

        OrdenEntity dbOrden = this.repository.save(query);

        return OrdenValueMapper.OrdenFacturacionEntityToResponseDTO(dbOrden);
    }

    public List<RecepcionOrdenResponseDTO> getAllRecibo() {
        List<OrdenEntity> list = repository.findByRecepcionEstatusNotNull();
        return list.stream().map((x) -> OrdenValueMapper.OrdenEntityToRecepcionOrdenResponseDTO(x)).toList();
    }

    public RecepcionOrdenResponseDTO cambiarARecibo(UUID id, RecepcionEstatus accion) {
        OrdenEntity query = findById(id);
        query.setRecepcionEstatus(accion);
        query = repository.save(query);
        return OrdenValueMapper.OrdenEntityToRecepcionOrdenResponseDTO(query);
    }

    @Transactional
    public RecepcionOrdenResponseDTO updateRecibo(RecepcionOrdenRequestDTO dto) {
        OrdenEntity query = findById(dto.getIdOrden());

        int[] indice = {0};
        List<PartidaOrdenEntity> partidas = query.getPartidas().stream()
                .map((row) -> {
                    Almacen almacen = almacenRepository.findById(dto.getPartidas().get(indice[0]).getAlmacenId())
                            .orElseThrow( () -> new RuntimeException("No se encontro la Id de almacen"));
                    row.setAlmacen(almacen);
                    row.setComentario(dto.getPartidas().get(indice[0]).getComentario());

                    //TODO Que valide que no superen al numero que se pidio.

                    row.setUnidadesRecibidas(dto.getPartidas().get(indice[0]).getUnidadesRecibidas());


                    // Crear la partida y asociarla a la orden
                    indice[0]++;
                    return row;
                })
                .collect(Collectors.toList()); // Recolectar las partidas en una lista
        query.setComentarioRecibo(dto.getComentarioRecibo());
        query.setFechaRecibo(LocalDateTime.now());
        return OrdenValueMapper.OrdenEntityToRecepcionOrdenResponseDTO(repository.save(query));


    }

    public Specification<OrdenEntity> filterWithParameters(Map<String, String> params) {
        return new OrdenSpecification().getSpecificationByFilters(params);
    }

    private String generarFolio() {
        try {
            return jdbcTemplate.queryForObject("SELECT generar_id()", String.class);
        } catch (Exception e) {
            throw new IDNotFound("Hubo un error al generar el folio" + e.getMessage());
        }

    }

    public SolicitudOrdenResponseDTO cambiarEstado(UUID id, SolicitanteEstatus accion) {
        OrdenEntity query = findById(id);
        switch (accion) {
            case ORDEN_COMPLETADA -> {
                query.setPostAutorizadoEstatus(FacturacionOrdenEstatus.PENDIENTE);
            }

        }
        query.setSolicitanteEstatus(accion);
        query = repository.save(query);
        return OrdenValueMapper.OrdenEntityToResponseDTO(query, query.getPartidas());
    }

    private OrdenEntity findById(UUID id) {
        return repository.findByIdAndDeletedFalse(id).orElseThrow(() -> new IDNotFound("Error: ID no encontrado"));
    }


    public List<Prioridad> getPriodidades() {
        return Arrays.stream(Prioridad.values()).toList();
    }
}

