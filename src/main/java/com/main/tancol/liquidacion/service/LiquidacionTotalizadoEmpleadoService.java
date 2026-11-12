package com.main.tancol.liquidacion.service;

import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.empleados.repositories.EmpleadosRepository;
import com.main.tancol.liquidacion.dto.request.LiquidacionTotalizadoEmpleadoRequestDTO;
import com.main.tancol.liquidacion.dto.response.LiquidacionTotalizadoEmpleadoResponseDTO;
import com.main.tancol.liquidacion.entities.LiquidacionTotalizadoEmpleadoEntity;
import com.main.tancol.liquidacion.entities.LiquidacionTotalizadoEntity;
import com.main.tancol.liquidacion.repositories.EmpleadoTotalizadoLiquidacionRepository;
import com.main.tancol.liquidacion.util.totalizados.LiquidacionTotalizadoEmpleadoSpecification;
import com.main.tancol.liquidacion.util.totalizados.LiquidacionTotalizadoEmpleadoValueMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class LiquidacionTotalizadoEmpleadoService implements com.main.tancol.liquidacion.service.interfaces.LiquidacionTotalizadoEmpleadoService {

    private final EmpleadoTotalizadoLiquidacionRepository repository;
    private final EmpleadosRepository empleadosRepository;


    @Override
    public LiquidacionTotalizadoEmpleadoResponseDTO create(LiquidacionTotalizadoEmpleadoRequestDTO dto) {
        return null;
    }

    @Override
    public LiquidacionTotalizadoEmpleadoResponseDTO getById(UUID id) {
        return null;
    }

    @Override
    public LiquidacionTotalizadoEmpleadoResponseDTO update(UUID id, LiquidacionTotalizadoEmpleadoRequestDTO dto) {
        return null;
    }

    @Override
    public LiquidacionTotalizadoEmpleadoResponseDTO delete(UUID id) {
        return null;
    }

    @Override
    public List<LiquidacionTotalizadoEmpleadoResponseDTO> getAll(Map<String, String> params) {
        Sort sort = null;
        // sort = Sort.by(Sort.Direction.ASC, "createdAt");
        List<LiquidacionTotalizadoEmpleadoEntity> list = repository.findAll(filterWithParameters(params), sort);
        return list.stream().map(LiquidacionTotalizadoEmpleadoValueMapper::LiquidacionTotalizadoEmpleadoEntityToResponse).toList();
    }

    public Specification<LiquidacionTotalizadoEmpleadoEntity> filterWithParameters(Map<String, String> params) {
        return new LiquidacionTotalizadoEmpleadoSpecification().getSpecificationByFilters(params);
    }

    public List<LiquidacionTotalizadoEmpleadoEntity> createChoferesForTotalizado(
            LiquidacionTotalizadoEntity liquidacion, // Debe estar gestionado (attached) por el EM
            List<LiquidacionTotalizadoEmpleadoRequestDTO> choferesDtos) {

        if (choferesDtos == null || choferesDtos.isEmpty()) {
            return new ArrayList<>();
        }

        List<LiquidacionTotalizadoEmpleadoEntity> choferesCreados = new ArrayList<>();

        for (LiquidacionTotalizadoEmpleadoRequestDTO choferDto : choferesDtos) {
            // 1. Obtener la entidad Empleado para el chofer
            EmpleadosEntity empleadoChofer = empleadosRepository.findById(choferDto.getIdEmpleado())
                    .orElseThrow(() -> new IDNotFound("Empleado chofer no encontrado: " + choferDto.getIdEmpleado()));

            // 2. Crear la entidad LiquidacionTotalizadoEmpleadoEntity para el chofer
            LiquidacionTotalizadoEmpleadoEntity choferEntity = LiquidacionTotalizadoEmpleadoEntity.builder()
                    .empleado(empleadoChofer)
                    .cantidad(choferDto.getTotal())
                    .origenTotalizado(liquidacion) // Establece la relación con el Totalizado
                    // choferOrigen se deja null para choferes principales
                    // ayudantes se inicializa vacía por la entidad, la poblaremos después
                    .chofer(true)
                    .build();
            choferEntity.setAyudantes(new LinkedList<>());

            log.info("Creando chofer: {}", empleadoChofer.getNombre());

            // 3. Procesar y asociar los ayudantes si existen
            if (choferDto.getAyudantes() != null && !choferDto.getAyudantes().isEmpty()) {
                for (LiquidacionTotalizadoEmpleadoRequestDTO ayudanteDto : choferDto.getAyudantes()) {
                    // 3a. Obtener la entidad Empleado para el ayudante
                    EmpleadosEntity empleadoAyudante = empleadosRepository.findById(ayudanteDto.getIdEmpleado())
                            .orElseThrow(() -> new IDNotFound("Empleado ayudante no encontrado: " + ayudanteDto.getIdEmpleado()));

                    // 3b. Crear la entidad LiquidacionTotalizadoEmpleadoEntity para el ayudante
                    LiquidacionTotalizadoEmpleadoEntity ayudanteEntity = LiquidacionTotalizadoEmpleadoEntity.builder()
                            .empleado(empleadoAyudante)
                            .cantidad(ayudanteDto.getTotal())
                            .origenTotalizado(liquidacion) // Tambien pertenece al mismo totalizado
                            .chofer(false)
                            .choferOrigen(choferEntity) // Establece la relación con el chofer (FK)
                            .build();
                    log.info("  - Agregando ayudante: {}", empleadoAyudante.getNombre());

                    // 3c. Asociar el ayudante al chofer usando el metodo helper
                    // Esto actualiza AMBOS lados de la relación bidireccional en memoria
                    choferEntity.addAyudante(ayudanteEntity);
                }
            }

            // 4. Agregar el chofer completamente construido (con sus ayudantes) a la lista de resultados
            choferesCreados.add(choferEntity);
        }

        // Devuelve solo la lista de choferes. Las relaciones ayudante->chofer ya están establecidas.
        // La persistencia de toda la estructura (choferes y sus ayudantes) depende de la configuración de CascadeType
        // en la relación @OneToMany(mappedBy = "choferOrigen", cascade = CascadeType.ALL...) de LiquidacionTotalizadoEmpleadoEntity.
        return choferesCreados;
    }


}
