package com.main.tancol.logistica.service.bitacoras;

import com.main.tancol.common.dto.CatalogPage;
import com.main.tancol.common.dto.PageResponse;
import com.main.tancol.common.exceptions.exeption.FilterNotFound;
import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.logistica.dto.request.BitacoraVentaRequestDTO;
import com.main.tancol.logistica.dto.response.BitacoraVentaListResponseDTO;
import com.main.tancol.logistica.dto.response.BitacoraVentaResponseDTO;
import com.main.tancol.logistica.entities.bitacora.BitacoraVentaRutaEntity;
import com.main.tancol.logistica.enums.EstatusBitacoraVenta;
import com.main.tancol.logistica.repositories.BitacoraVentaRepository;
import com.main.tancol.logistica.util.BitacoraVentaPageSpecification;
import com.main.tancol.logistica.util.BitacoraVentaSpecification;
import com.main.tancol.logistica.util.BitacoraVentaValueMapper;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BitacoraVentaService {

    private final BitacoraVentaRepository bitacoraVentaRepository;
    private final JdbcTemplate jdbcTemplate;


    @Transactional
    public BitacoraVentaResponseDTO create(BitacoraVentaRequestDTO dto) {

        boolean creadaHoy = bitacoraVentaRepository.existsByFecha(dto.getFecha());
        if (!creadaHoy) {
            log.info("Creando Bitacora de"+ dto.getFecha());
            BitacoraVentaRutaEntity bc = BitacoraVentaRutaEntity.builder()
                    .estatusBitacoraVenta(EstatusBitacoraVenta.BORRADOR)
                    .folio(generarFolio())
//                    .partidas(new LinkedHashSet<>())
                    .build();

            bc = bitacoraVentaRepository.save(bc);
        return BitacoraVentaValueMapper.BitacoraVentaEntityToResponse(bc);
        }else {
            throw new IDNotFound("Ya existe creada una bitacora para este dia");
        }

    }
    @Transactional
    public BitacoraVentaResponseDTO update(UUID id, Object dto) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public BitacoraVentaResponseDTO getById(UUID id) {
        return BitacoraVentaValueMapper.BitacoraVentaEntityToResponse(this.bitacoraVentaRepository
                .findById(id).orElseThrow(()-> new IDNotFound("No se encontro el id")));
    }

    public List<BitacoraVentaResponseDTO> getAll(Map<String, String> params) {
        List<BitacoraVentaRutaEntity> list = bitacoraVentaRepository.findAll(filterWithParameters(params));
        return list.stream().map(BitacoraVentaValueMapper::BitacoraVentaEntityToResponse).toList();

    }

    public Specification<BitacoraVentaRutaEntity> filterWithParameters(Map<String, String> params) {
        return new BitacoraVentaSpecification().getSpecificationByFilters(params);
    }

    // ------------------------------------------------------------------ pagina (GET /bitacoras-rutas/page)

    private static final Set<String> PAGE_FILTERS = Set.of("fecha", "folio", "status", "partidas", "createdAt", "updatedAt");
    private static final Map<String, String> PAGE_SORTS = Map.of(
            "fecha", "fecha",
            "folio", "folio",
            "status", "estatusBitacoraVenta",
            "createdAt", "createdAt",
            "updatedAt", "updatedAt"
    );
    private static final String SORT_PARTIDAS = "partidas";
    private static final Set<String> PAGE_SORT_KEYS = Set.of("fecha", "folio", "status", "createdAt", "updatedAt", SORT_PARTIDAS);

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public PageResponse<BitacoraVentaListResponseDTO> getPage(Map<String, String> params) {
        CatalogPage page = CatalogPage.parse(params, PAGE_FILTERS, PAGE_SORT_KEYS);

        Sort.Direction partidasSort = null;
        Sort sort;
        if (SORT_PARTIDAS.equals(page.sortKey())) {
            String direction = page.sortDirection() == null ? "" : page.sortDirection().trim();
            if (!direction.equals("ASC") && !direction.equals("DESC")) {
                throw new FilterNotFound("Invalid filter: sort_" + page.sortKey());
            }
            partidasSort = Sort.Direction.valueOf(direction);
            sort = Sort.unsorted();
        } else {
            sort = page.sort(PAGE_SORTS, Sort.by(Sort.Direction.DESC, "createdAt"), "idBitacora");
        }

        Page<BitacoraVentaRutaEntity> result = bitacoraVentaRepository.findAll(
                new BitacoraVentaPageSpecification(page.search(), page.filters(), partidasSort),
                page.pageable(sort)
        );

        List<BitacoraVentaRutaEntity> entities = result.getContent();
        Map<UUID, Long> partidas = new HashMap<>();
        if (!entities.isEmpty()) {
            List<UUID> ids = entities.stream().map(BitacoraVentaRutaEntity::getIdBitacora).toList();
            for (Object[] row : bitacoraVentaRepository.contarPartidas(ids)) {
                partidas.put((UUID) row[0], row[1] == null ? 0L : ((Number) row[1]).longValue());
            }
        }

        List<BitacoraVentaListResponseDTO> items = entities.stream()
                .map(entity -> new BitacoraVentaListResponseDTO(
                        entity.getIdBitacora(),
                        entity.getFolio(),
                        entity.getFecha() != null ? entity.getFecha().atStartOfDay() : null,
                        entity.getEstatusBitacoraVenta(),
                        partidas.getOrDefault(entity.getIdBitacora(), 0L),
                        entity.getCreatedAt(),
                        entity.getUpdatedAt()
                ))
                .toList();

        return new PageResponse<>(items, result.getTotalElements(), page.page(), page.pageSize());
    }
    private String generarFolio() {
        try {
            return jdbcTemplate.queryForObject("SELECT generar_folio(4)", String.class);
        } catch (Exception e) {
            throw new IDNotFound("Hubo un error al generar el folio" + e.getMessage());
        }

    }
    public ResponseEntity<?> findByFecha(LocalDate fecha) {

        boolean creadaHoy = bitacoraVentaRepository.existsByFecha(fecha);
        BitacoraVentaRutaEntity bc = null;
        if (creadaHoy) {
            log.info("Bitacora de venta ya existente");
            bc = bitacoraVentaRepository.findByFecha(fecha);
            return ResponseEntity.ok(BitacoraVentaValueMapper.BitacoraVentaEntityToResponse(bc));

        }
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<?> delete(@Valid UUID id) {
        BitacoraVentaRutaEntity bitacora = this.bitacoraVentaRepository.findById(id).orElseThrow(()-> new IDNotFound("No se encontro el id"));
        this.bitacoraVentaRepository.delete(bitacora);
        return ResponseEntity.noContent().build();
    }
}
