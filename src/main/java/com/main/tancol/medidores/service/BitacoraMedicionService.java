package com.main.tancol.medidores.service;

import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.empleados.helpers.EmpleadoContext;
import com.main.tancol.medidores.dto.request.BitacoraMedicionRequestDTO;
import com.main.tancol.medidores.dto.response.BitacoraMedicionResponseDTO;
import com.main.tancol.medidores.entities.BitacoraMedicionEntity;
import com.main.tancol.medidores.entities.MedidoresEntity;
import com.main.tancol.medidores.repositories.BitacoraMedicionRepository;
import com.main.tancol.medidores.repositories.MedidoresRepository;
import com.main.tancol.medidores.service.interfaces.IBitacoraMedicionService;
import com.main.tancol.medidores.util.BitacoraMedicionSpecification;
import com.main.tancol.medidores.util.BitacoraMedicionValueMapper;
import com.main.tancol.medidores.util.MedidorEstadoContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BitacoraMedicionService implements IBitacoraMedicionService {

    private final BitacoraMedicionRepository repository;
    private final MedidoresRepository medidoresRepository;
    private final MedidorEstadoService medidorEstadoService;


    @Override
    @Transactional
    public BitacoraMedicionResponseDTO create(BitacoraMedicionRequestDTO dto) {
        MedidoresEntity medidor = medidoresRepository.findById(dto.getIdMedidor()).orElseThrow(() -> new IDNotFound("Medidor no encontrado"));

        EmpleadosEntity emp = EmpleadoContext.getEmpleadoActual();

        BitacoraMedicionEntity registro = BitacoraMedicionEntity.builder()
                .medidor(medidor)
                .medidaInicial(medidor.getMedicion())
                .medidaActual(dto.getMedicionActual())
                .consumo(calcularConsumo(medidor.getIdMedidor(), medidor.getMedicion(), dto.getMedicionActual()))
                .liquidado(dto.getLiquidado())
                .quedadoEnPipa(dto.getQuedadoEnPipa())
                .litrosMinuto(dto.getLitrosMinuto())
                .comentario(dto.getComentario())
                .nombreUsuario(ObjectUtils.isNotEmpty(emp)? emp.getNombreUsuario() : "Sin Usuario")
                .build();

        medidor.setMedicion(dto.getMedicionActual());
        medidor.setFechaUltimaMedicion(LocalDateTime.now());
        medidoresRepository.save(medidor);

        BitacoraMedicionEntity guardado = repository.save(registro);
        MedidoresEntity medidorConHijos = medidoresRepository.findById(medidor.getIdMedidor()).orElse(medidor);
        guardado.setMedidor(medidorConHijos);
        MedidorEstadoContext estado = medidorEstadoService.construirContexto(List.of(medidorConHijos));
        return BitacoraMedicionValueMapper.BitacoraMedicionEntityToResponse(guardado, estado);
    }

    private BigDecimal calcularConsumo(UUID idMedidor, BigDecimal medicionActualMedidor, BigDecimal medicionNueva) {
        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        BigDecimal medicionDiaAnterior = repository
                .findTopByMedidor_IdMedidorAndCreatedAtBeforeOrderByCreatedAtDesc(idMedidor, inicioDia)
                .map(BitacoraMedicionEntity::getMedidaActual)
                .orElse(medicionActualMedidor != null ? medicionActualMedidor : BigDecimal.ZERO);
        return medicionNueva.subtract(medicionDiaAnterior);
    }

    @Override
    public BitacoraMedicionResponseDTO getById(UUID id) {
        return null;
    }

    @Override
    public BitacoraMedicionResponseDTO update(UUID id, BitacoraMedicionRequestDTO dto) {
        return null;
    }

    @Override
    public BitacoraMedicionResponseDTO delete(UUID id) {
        return null;
    }

    @Override
    public List<BitacoraMedicionResponseDTO> getAll(Map<String, String> params) {
        Sort sort = Sort.by(Sort.Direction.ASC, "createdAt");
        List<BitacoraMedicionEntity> list = repository.findAll(filterWithParameters(params), sort);
        Set<UUID> medidorIds = list.stream()
                .map(BitacoraMedicionEntity::getMedidor)
                .filter(Objects::nonNull)
                .map(MedidoresEntity::getIdMedidor)
                .collect(Collectors.toSet());
        List<MedidoresEntity> medidores = medidorIds.isEmpty()
                ? List.of()
                : medidoresRepository.findByIdMedidorIn(medidorIds);
        MedidorEstadoContext estado = medidorEstadoService.construirContexto(medidores);
        var medidoresPorId = medidores.stream()
                .collect(Collectors.toMap(MedidoresEntity::getIdMedidor, m -> m));
        return list.stream()
                .map(bitacora -> {
                    if (bitacora.getMedidor() != null) {
                        MedidoresEntity medidor = medidoresPorId.get(bitacora.getMedidor().getIdMedidor());
                        if (medidor != null) {
                            bitacora.setMedidor(medidor);
                        }
                    }
                    return BitacoraMedicionValueMapper.BitacoraMedicionEntityToResponse(bitacora, estado);
                })
                .toList();
    }

    public Specification<BitacoraMedicionEntity> filterWithParameters(Map<String, String> params) {
        return new BitacoraMedicionSpecification().getSpecificationByFilters(params);
    }
}
