package com.main.tancol.liquidacion.service;

import com.main.tancol.liquidacion.dto.request.LiquidacionDistribucionDescuentoRequestDTO;
import com.main.tancol.liquidacion.dto.response.LiquidacionDistribucionDescuentoResponseDTO;
import com.main.tancol.liquidacion.entities.LiquidacionDistribucionDescuentoEntity;
import com.main.tancol.liquidacion.enums.EstadosLiquidacion;
import com.main.tancol.liquidacion.repositories.LiquidacionDistribucionDescuentoRepository;
import com.main.tancol.liquidacion.service.interfaces.ILiquidacionDistribucionDescuentoService;
import com.main.tancol.liquidacion.util.LiquidacionDistribucionDescuentoSpecification;
import com.main.tancol.liquidacion.util.LiquidacionDistribucionDescuentoValueMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LiquidacionDistribucionDescuentoService implements ILiquidacionDistribucionDescuentoService {

    private final LiquidacionDistribucionDescuentoRepository repository;


    @Override
    public LiquidacionDistribucionDescuentoResponseDTO create(LiquidacionDistribucionDescuentoRequestDTO dto) {
        return null;
    }

    @Override
    public LiquidacionDistribucionDescuentoResponseDTO getById(UUID id) {
        return null;
    }

    @Override
    public LiquidacionDistribucionDescuentoResponseDTO update(UUID id, LiquidacionDistribucionDescuentoRequestDTO dto) {
        return null;
    }

    @Override
    public LiquidacionDistribucionDescuentoResponseDTO delete(UUID id) {
        return null;
    }

    @Override
    public List<LiquidacionDistribucionDescuentoResponseDTO> getAll(Map<String, String> params) {
        Sort sort = null;
        // sort = Sort.by(Sort.Direction.ASC, "createdAt");
        List<LiquidacionDistribucionDescuentoEntity> list = repository.findByLiquidacionPartidaEntity_LiquidacionOrigen_EstadosLiquidacion(EstadosLiquidacion.FINALIZADA);
        return list.stream().map(LiquidacionDistribucionDescuentoValueMapper::LiquidacionDistribucionDescuentoEntityToResponse).toList();
    }

    public Specification<LiquidacionDistribucionDescuentoEntity> filterWithParameters(Map<String, String> params) {
        return new LiquidacionDistribucionDescuentoSpecification().getSpecificationByFilters(params);
    }
}
