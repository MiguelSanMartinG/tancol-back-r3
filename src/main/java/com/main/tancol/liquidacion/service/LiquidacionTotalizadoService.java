package com.main.tancol.liquidacion.service;

import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.liquidacion.dto.request.LiquidacionTotalizadoRequestDTO;
import com.main.tancol.liquidacion.dto.response.LiquidacionTotalizadoResponseDTO;
import com.main.tancol.liquidacion.entities.LiquidacionEntity;
import com.main.tancol.liquidacion.entities.LiquidacionTotalizadoEntity;
import com.main.tancol.liquidacion.repositories.TotalizadoChoferesRepository;
import com.main.tancol.liquidacion.service.interfaces.ILiquidacionTotalizadoService;
import com.main.tancol.liquidacion.util.totalizados.LiquidacionTotalizadoSpecification;
import com.main.tancol.liquidacion.util.totalizados.LiquidacionTotalizadoValueMapper;
import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
import com.main.tancol.producto.repositories.ProductoComercialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class LiquidacionTotalizadoService implements ILiquidacionTotalizadoService {

    private final TotalizadoChoferesRepository repository;
    private final ProductoComercialRepository productoComercialRepository;
    private final LiquidacionTotalizadoEmpleadoService liquidacionTotalizadoEmpleadoService;


    @Override
    public LiquidacionTotalizadoResponseDTO create(LiquidacionTotalizadoRequestDTO dto) {
        return null;
    }

    @Override
    public LiquidacionTotalizadoResponseDTO getById(UUID id) {
        return null;
    }

    @Override
    public LiquidacionTotalizadoResponseDTO update(UUID id, LiquidacionTotalizadoRequestDTO dto) {
        return null;
    }

    @Override
    public LiquidacionTotalizadoResponseDTO delete(UUID id) {
        return null;
    }

    @Override
    public List<LiquidacionTotalizadoResponseDTO> getAll(Map<String, String> params) {
        Sort sort = null;
        // sort = Sort.by(Sort.Direction.ASC, "createdAt");
        List<LiquidacionTotalizadoEntity> list = repository.findAll(filterWithParameters(params), sort);
        return list.stream().map(LiquidacionTotalizadoValueMapper::LiquidacionTotalizadoEntityToResponse).toList();
    }

    public Specification<LiquidacionTotalizadoEntity> filterWithParameters(Map<String, String> params) {
        return new LiquidacionTotalizadoSpecification().getSpecificationByFilters(params);
    }

    public void create(LiquidacionEntity entity, List<LiquidacionTotalizadoRequestDTO> totalizados) {

        if(totalizados == null) return;

        entity.getTotalesPorProductos().clear();

        for (LiquidacionTotalizadoRequestDTO dto : totalizados) {

            ProductoComercialEntity producto = null;
            if (dto.getIdProducto() != null) {
                producto = productoComercialRepository.findById(dto.getIdProducto()).orElseThrow(() -> new IDNotFound("No se encontró el producto"));
            }

            LiquidacionTotalizadoEntity liquidacion = LiquidacionTotalizadoEntity.builder()
                    .productoComercial(producto)
                    .build();
            liquidacion.setEmpleadoTotalizadoLiquidacion(new LinkedList<>(liquidacionTotalizadoEmpleadoService.createChoferesForTotalizado(liquidacion, dto.getChoferes())));


            entity.agregarTotalizado(liquidacion);
        }

    }

    public void update(LiquidacionEntity liquidacion, List<LiquidacionTotalizadoRequestDTO> dto) {

            liquidacion.clearTotalizados();
            create(liquidacion, dto);

    }

    private void quitarSobrantes(LiquidacionEntity liquidacion, Set<UUID> conjuntoConId) {
        liquidacion.getLiquidacionPartida().removeIf(x ->
                !conjuntoConId.contains(x.getIdPartida())
        );
    }
}
