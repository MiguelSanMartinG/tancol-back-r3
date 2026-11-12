package com.main.tancol.medidores.service;

import com.main.tancol.almacen.entities.UnidadesMedidaEntity;
import com.main.tancol.almacen.repositories.UnidadesMedidaRepository;
import com.main.tancol.common.dto.CatalogPage;
import com.main.tancol.common.dto.PageResponse;
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

    public void resetMedidor(UUID id, String comentario){
        try {
            MedidoresEntity medidor = this.repository.findById(id).orElseThrow( () -> new IDNotFound("No se encontro la id del medidor"));
            medidor.resetearMedidor();
            BitacoraMedicionRequestDTO rq = new BitacoraMedicionRequestDTO(medidor.getIdMedidor(), new BigDecimal(0), comentario, null, null, null);
            this.bitacoraMedicionService.create(rq);
            this.repository.save(medidor);
        }catch (Exception e){
            throw new RuntimeException(e);
        }


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
        return enriquecerRespuesta(repository.save(medidor));

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
