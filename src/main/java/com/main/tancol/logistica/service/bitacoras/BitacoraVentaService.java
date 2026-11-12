package com.main.tancol.logistica.service.bitacoras;

import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.logistica.dto.request.BitacoraVentaRequestDTO;
import com.main.tancol.logistica.dto.response.BitacoraVentaResponseDTO;
import com.main.tancol.logistica.entities.bitacora.BitacoraVentaRutaEntity;
import com.main.tancol.logistica.enums.EstatusBitacoraVenta;
import com.main.tancol.logistica.repositories.BitacoraVentaRepository;
import com.main.tancol.logistica.util.BitacoraVentaSpecification;
import com.main.tancol.logistica.util.BitacoraVentaValueMapper;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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
