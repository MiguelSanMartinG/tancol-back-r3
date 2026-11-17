package com.main.tancol.medidores.service;

import com.main.tancol.medidores.dto.request.ResetMedidorRequestDTO;
import com.main.tancol.medidores.entities.BitacoraMedicionEntity;
import com.main.tancol.medidores.repositories.BitacoraMedicionRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.main.tancol.almacen.entities.UnidadesMedidaEntity;
import com.main.tancol.almacen.repositories.UnidadesMedidaRepository;
import com.main.tancol.common.dto.CatalogPage;
import com.main.tancol.common.dto.PageResponse;
import com.main.tancol.common.exceptions.exeption.GenericExeption;
import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.medidores.dto.request.BitacoraMedicionRequestDTO;
import com.main.tancol.medidores.dto.request.MedidoresRequestDTO;
import com.main.tancol.medidores.dto.response.MedidoresResponseDTO;
import com.main.tancol.medidores.entities.MedidoresEntity;
import com.main.tancol.medidores.entities.SistemasDeAlmacenamientoEntity;
import com.main.tancol.medidores.repositories.MedidoresRepository;
import com.main.tancol.medidores.repositories.SistemasDeAlmacenamientoRepository;
import com.main.tancol.medidores.service.interfaces.IMedidoresService;
import com.main.tancol.medidores.util.MedidorEstadoContext;
import com.main.tancol.medidores.util.MedidoresPageSpecification;
import com.main.tancol.medidores.util.MedidoresSpecification;
import com.main.tancol.medidores.util.MedidoresValueMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedidoresService implements IMedidoresService {

    private final MedidoresRepository repository;
    private final SistemasDeAlmacenamientoRepository sistemasDeAlmacenamientoRepository;
    private final UnidadesMedidaRepository unidadesMedidaRepository;
    private final BitacoraMedicionService bitacoraMedicionService;
    private final MedidorEstadoService medidorEstadoService;
    private final BitacoraMedicionRepository bitacoraMedicionRepository;

    static final java.time.ZoneId ZONA_MEXICO = java.time.ZoneId.of("America/Mexico_City");


    private Set<SistemasDeAlmacenamientoEntity> resolveSistemasDeAlmacenamiento(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return new LinkedHashSet<>();
        }
        List<UUID> distinctIds = ids.stream().distinct().toList();
        List<SistemasDeAlmacenamientoEntity> found = (List<SistemasDeAlmacenamientoEntity>) sistemasDeAlmacenamientoRepository.findAllById(distinctIds);
        if (found.size() != distinctIds.size()) {
            throw new IDNotFound("No se encontro algún sistema de almacenamiento");
        }
        return new LinkedHashSet<>(found);
    }

    @Override
    @Transactional
    public MedidoresResponseDTO create(MedidoresRequestDTO dto) {
        List<UUID> idsSistemas = dto.getIdsSistemasAlmacenamiento() != null ? dto.getIdsSistemasAlmacenamiento() : List.of();
        Set<SistemasDeAlmacenamientoEntity> sistemas = resolveSistemasDeAlmacenamiento(idsSistemas);
        UnidadesMedidaEntity u = unidadesMedidaRepository.findById(dto.getUnidadMedicion()).orElseThrow(() -> new IDNotFound("No se encontro la uuid de unidad de medicion"));
        validarAlertaConsumo(dto.getAlertaConsumoLitros());

        MedidoresEntity medidor = MedidoresValueMapper.MedidoresRequestToEntity(dto, u, sistemas);
        return enriquecerRespuesta(this.repository.save(medidor));
    }

    @Override
    public MedidoresResponseDTO getById(UUID id) {
        MedidoresEntity medidor = this.repository.findById(id).orElseThrow( () -> new IDNotFound("No se encontro la id del medidor"));
        return enriquecerRespuesta(medidor);
    }

    private MedidoresResponseDTO enriquecerRespuesta(MedidoresEntity medidor) {
        cargarHijosSiFaltan(List.of(medidor));
        MedidorEstadoContext estado = medidorEstadoService.construirContexto(List.of(medidor));
        return MedidoresValueMapper.MedidoresEntityToResponse(medidor, estado);
    }

    private void cargarHijosSiFaltan(List<MedidoresEntity> medidores) {
        Set<UUID> padreIds = medidores.stream()
                .map(MedidoresEntity::getIdMedidor)
                .collect(Collectors.toSet());
        if (padreIds.isEmpty()) {
            return;
        }
        List<MedidoresEntity> hijos = repository.findByMedidorPadre_IdMedidorIn(padreIds);
        Map<UUID, Set<MedidoresEntity>> hijosPorPadre = new HashMap<>();
        for (MedidoresEntity hijo : hijos) {
            if (hijo.getMedidorPadre() != null) {
                hijosPorPadre
                        .computeIfAbsent(hijo.getMedidorPadre().getIdMedidor(), k -> new LinkedHashSet<>())
                        .add(hijo);
            }
        }
        for (MedidoresEntity padre : medidores) {
            if (padre.getMedidoresHijos() == null || padre.getMedidoresHijos().isEmpty()) {
                Set<MedidoresEntity> hijosPadre = hijosPorPadre.get(padre.getIdMedidor());
                if (hijosPadre != null) {
                    padre.setMedidoresHijos(hijosPadre);
                }
            }
        }
    }

    /**
     * Reinicio de un medidor físico: se cierra con su última lectura real y lo reemplaza otro
     * medidor ya dado de alta, que toma su lugar en el árbol (padre e hijos) y su alerta de consumo
     * si no tiene una. El medidor reemplazado ya no recibe lecturas.
     */
    @Transactional
    public void resetMedidor(ResetMedidorRequestDTO dto) {
        MedidoresEntity anterior = this.repository.findById(dto.getIdMedidor())
                .filter(m -> !m.isDeleted())
                .orElseThrow(() -> new IDNotFound("No se encontró el medidor a reiniciar"));
        MedidoresEntity nuevo = this.repository.findById(dto.getIdMedidorReemplazo())
                .filter(m -> !m.isDeleted())
                .orElseThrow(() -> new IDNotFound("No se encontró el medidor de reemplazo"));
        validarReemplazo(anterior, nuevo, dto.getMedicionAnterior());

        String comentario = "Cierre por reemplazo: lo reemplaza " + etiqueta(nuevo) + "."
                + (dto.getMensaje() != null && !dto.getMensaje().isBlank() ? " " + dto.getMensaje().trim() : "");
        registrarLecturaDeCierre(anterior, dto.getMedicionAnterior(), comentario);

        MedidoresEntity padre = anterior.getMedidorPadre();
        if (padre != null) {
            padre.getMedidoresHijos().remove(anterior);
            anterior.setMedidorPadre(null);
            nuevo.setMedidorPadre(padre);
            padre.getMedidoresHijos().add(nuevo);
        }
        List<MedidoresEntity> hijos = new ArrayList<>(anterior.getMedidoresHijos());
        for (MedidoresEntity hijo : hijos) {
            hijo.setMedidorPadre(nuevo);
            nuevo.getMedidoresHijos().add(hijo);
        }
        anterior.getMedidoresHijos().clear();

        if (nuevo.getAlertaConsumoLitros() == null) {
            nuevo.setAlertaConsumoLitros(anterior.getAlertaConsumoLitros());
        }
        LocalDateTime ahora = LocalDateTime.now(ZONA_MEXICO);
        anterior.setMedidorReemplazadoPor(nuevo);
        anterior.setFechaReemplazo(ahora);
        nuevo.setReemplazaA(anterior);
        nuevo.setFechaReemplazo(ahora);

        this.repository.save(nuevo);
        hijos.forEach(this.repository::save);
        this.repository.save(anterior);
    }

    static void validarReemplazo(MedidoresEntity anterior, MedidoresEntity nuevo, BigDecimal medicionAnterior) {
        if (anterior.getIdMedidor().equals(nuevo.getIdMedidor())) {
            throw new GenericExeption("El medidor de reemplazo debe ser otro medidor.");
        }
        if (anterior.getMedidorReemplazadoPor() != null) {
            throw new GenericExeption("El medidor " + etiqueta(anterior) + " ya fue reemplazado por "
                    + etiqueta(anterior.getMedidorReemplazadoPor()) + ".");
        }
        if (!nuevo.isEnabled() || nuevo.getMedidorReemplazadoPor() != null) {
            throw new GenericExeption("El medidor " + etiqueta(nuevo) + " no está activo o ya fue reemplazado.");
        }
        if (nuevo.getMedidorPadre() != null || (nuevo.getMedidoresHijos() != null && !nuevo.getMedidoresHijos().isEmpty())) {
            throw new GenericExeption("El medidor " + etiqueta(nuevo)
                    + " ya está en el árbol de medidores; elige un medidor sin padre ni hijos.");
        }
        if (medicionAnterior == null || medicionAnterior.signum() < 0) {
            throw new GenericExeption("La medición anterior es obligatoria.");
        }
        BigDecimal ultima = anterior.getMedicion();
        if (ultima != null && medicionAnterior.compareTo(ultima) < 0) {
            throw new GenericExeption("La medición anterior no puede ser menor que la última lectura del medidor ("
                    + ultima.multiply(BigDecimal.valueOf(1000)).stripTrailingZeros().toPlainString() + " LTS).");
        }
    }

    /**
     * Lectura con la que se cierra el medidor reemplazado. Si ya tenía lectura hoy, se corrige esa
     * (para no contar dos veces el consumo del día); si no, se registra una nueva.
     */
    private void registrarLecturaDeCierre(MedidoresEntity anterior, BigDecimal medicionAnterior, String comentario) {
        Optional<BitacoraMedicionEntity> ultima = bitacoraMedicionRepository
                .findTopByMedidor_IdMedidorAndCreatedAtBeforeOrderByCreatedAtDesc(
                        anterior.getIdMedidor(), LocalDateTime.now().plusDays(1));
        boolean hayLecturaHoy = ultima
                .map(b -> b.getCreatedAt() != null && b.getCreatedAt().toLocalDate().equals(LocalDate.now()))
                .orElse(false);
        if (hayLecturaHoy) {
            BitacoraMedicionEntity hoy = ultima.get();
            BigDecimal base = hoy.getConsumo() != null && hoy.getMedidaActual() != null
                    ? hoy.getMedidaActual().subtract(hoy.getConsumo())
                    : hoy.getMedidaInicial();
            hoy.setMedidaActual(medicionAnterior);
            hoy.setConsumo(medicionAnterior.subtract(base));
            hoy.setComentario(comentario);
            bitacoraMedicionRepository.save(hoy);
            anterior.setMedicion(medicionAnterior);
            anterior.setFechaUltimaMedicion(LocalDateTime.now());
        } else {
            bitacoraMedicionService.create(new BitacoraMedicionRequestDTO(
                    anterior.getIdMedidor(), medicionAnterior, comentario, null, null, null));
            anterior.setMedicion(medicionAnterior);
        }
    }

    static String etiqueta(MedidoresEntity m) {
        return m.getAlias() != null && !m.getAlias().isBlank() ? m.getNombre() + " (" + m.getAlias() + ")" : m.getNombre();
    }

    @Override
    public MedidoresResponseDTO update(UUID id, MedidoresRequestDTO dto) {
        MedidoresEntity medidor = this.repository.findById(id).orElseThrow( () -> new IDNotFound("No se encontro la id del medidor"));
        UnidadesMedidaEntity u = unidadesMedidaRepository.findById(dto.getUnidadMedicion()).orElseThrow(() -> new IDNotFound("No se encontro el idMedidor"));
//        medidor.setProceso(dto.getProceso());
        if(dto.getMedicionInicial() != null){
            throw new IDNotFound("Nota para luis, no puedes asignarle una nueva medida");
        }
        medidor.setNombre(dto.getNombre());
        medidor.setAlias(dto.getAlias());
        if (dto.getIdsSistemasAlmacenamiento() != null) {
            Set<SistemasDeAlmacenamientoEntity> sistemas = resolveSistemasDeAlmacenamiento(dto.getIdsSistemasAlmacenamiento());
            medidor.getSistemasDeAlmacenamiento().clear();
            medidor.getSistemasDeAlmacenamiento().addAll(sistemas);
        }
        medidor.setUnidadadMedidaDefault(u);
        medidor.setTipoAgua(dto.getTipoAgua());
        validarAlertaConsumo(dto.getAlertaConsumoLitros());
        medidor.setAlertaConsumoLitros(dto.getAlertaConsumoLitros());
        return enriquecerRespuesta(repository.save(medidor));

    }

    /** La alerta es opcional (null = sin alerta); si viene, no puede ser negativa. */
    private void validarAlertaConsumo(BigDecimal alertaConsumoLitros) {
        if (alertaConsumoLitros != null && alertaConsumoLitros.signum() < 0) {
            throw new GenericExeption("La alerta de consumo diario no puede ser negativa");
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> agregarHijo(UUID medidorPadre , UUID medidorHijo ){
        MedidoresEntity padre = this.repository.findById(medidorPadre).orElseThrow(() ->  new IDNotFound("No se encontro la ID: %s (Padre)".formatted(medidorPadre)));
        MedidoresEntity hijo = this.repository.findById(medidorHijo).orElseThrow(() ->  new IDNotFound("No se encontro la ID: %s (Hijo)".formatted(medidorHijo)));

        if(hijo.tienePadre()){
            throw new IDNotFound("El medidor hijo ya tiene un padre.");
        }

        padre.getMedidoresHijos().add(hijo);
        hijo.setMedidorPadre(padre);

        Map<String,String> respuesta = new HashMap<>();
        try {
            respuesta.put("respuesta","Operación exitosa");
            // La FK vive en el hijo (lado dueño), por eso se persiste el hijo.
            this.repository.save(hijo);
            return ResponseEntity.ok(respuesta);
        }catch (Error e){
            throw new IDNotFound(e.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> borrarHijo(UUID medidorPadre , UUID medidorHijo){
        MedidoresEntity padre = this.repository.findById(medidorPadre).orElseThrow(() ->  new IDNotFound("No se encontro la ID: %s (Padre)".formatted(medidorPadre)));
        MedidoresEntity hijo = this.repository.findById(medidorHijo).orElseThrow(() ->  new IDNotFound("No se encontro la ID: %s (Hijo)".formatted(medidorHijo)));

        if(!hijo.tienePadre()){
            throw new IDNotFound("El medidor hijo no tiene un padre.");
        }

        padre.getMedidoresHijos().remove(hijo);
        hijo.setMedidorPadre(null);

        Map<String,String> respuesta = new HashMap<>();
        try {
            respuesta.put("respuesta","Operación exitosa");
            // Se persiste el hijo para actualizar correctamente la FK medidor_padre_id_medidor.
            this.repository.save(hijo);
            return ResponseEntity.ok(respuesta);
//            return ResponseEntity.ok("Operación exitosa");
        }catch (Error e){
            throw new IDNotFound(e.getMessage());
        }
    }


    @Override
    public MedidoresResponseDTO delete(UUID id) {
        MedidoresEntity medidor = this.repository.findById(id).orElseThrow(
                () -> new IDNotFound("No se encontro la id del medidor"));
        medidor.setDeleted(true);
        return enriquecerRespuesta(this.repository.save(medidor));
    }

    @Override
    public List<MedidoresResponseDTO> getAll(Map<String, String> params) {
        List<MedidoresEntity> list = repository.findAll(filterWithParameters(params));
        cargarHijosSiFaltan(list);
        MedidorEstadoContext estado = medidorEstadoService.construirContexto(list);
        return list.stream()
                .map(m -> MedidoresValueMapper.MedidoresEntityToResponse(m, estado))
                .toList();
    }

    private static final Set<String> PAGE_FILTERS = Set.of(
            "nombre", "alias", "tipoAgua", "ultimaMedicion", "fechaUltimaMedicion", "createdAt", "updatedAt"
    );
    private static final Map<String, String> PAGE_SORTS = Map.of(
            "nombre", "nombre",
            "alias", "alias",
            "tipoAgua", "tipoAgua",
            "ultimaMedicion", "medicion",
            "fechaUltimaMedicion", "fechaUltimaMedicion",
            "createdAt", "createdAt",
            "updatedAt", "updatedAt"
    );

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public PageResponse<MedidoresResponseDTO> getPage(Map<String, String> params) {
        CatalogPage query = CatalogPage.parse(params, PAGE_FILTERS, PAGE_SORTS.keySet());
        var result = repository.findAll(
                new MedidoresPageSpecification(query.search(), query.filters()),
                query.pageable(query.sort(PAGE_SORTS, Sort.by(Sort.Direction.DESC, "createdAt"), "idMedidor"))
        );
        var items = result.getContent().stream()
                .map(m -> MedidoresValueMapper.MedidoresEntityToResponse(m, MedidorEstadoContext.VACIO))
                .toList();
        return new PageResponse<>(items, result.getTotalElements(), query.page(), query.pageSize());
    }

    public Specification<MedidoresEntity> filterWithParameters(Map<String, String> params) {
        return new MedidoresSpecification().getSpecificationByFilters(params);
    }

}
