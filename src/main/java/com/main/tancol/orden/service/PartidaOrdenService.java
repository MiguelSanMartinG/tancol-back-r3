package com.main.tancol.orden.service;

import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.orden.entities.OrdenEntity;
import com.main.tancol.orden.repositories.OrdenRepository;
import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import com.main.tancol.producto.repositories.ProductoRepository;
import com.main.tancol.proveedor.repositories.ImpuestosRepository;
import com.main.tancol.orden.dto.request.PartidaOrdenRequestDTO;
import com.main.tancol.orden.dto.response.PartidaOrdenResponseDTO;
import com.main.tancol.orden.entities.PartidaOrdenEntity;
import com.main.tancol.orden.repositories.PartidaOrdenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import com.main.tancol.orden.service.interfaces.IPartidaOrdenService;

import java.util.*;

@Slf4j
@Service
public class PartidaOrdenService implements IPartidaOrdenService {

    private final PartidaOrdenRepository repository;
    private final ImpuestosRepository impuestosRepository;
    private final ProductoRepository productoRepository;
    private final OrdenRepository ordenRepository;

    public PartidaOrdenService(PartidaOrdenRepository repository, ImpuestosRepository impuestosRepository, ProductoRepository productoRepository, OrdenRepository ordenRepository) {
        this.repository = repository;
        this.impuestosRepository = impuestosRepository;
        this.productoRepository = productoRepository;
        this.ordenRepository = ordenRepository;
    }


    @Override
    public PartidaOrdenEntity create(PartidaOrdenRequestDTO dto, OrdenEntity ordenEntity) {

//        TipoImpuesto tImpuesto = impuestosRepository.findById(dto.getIdTipoImpuesto()).orElseThrow( () -> new IDNotFound("No existe el tipo de impuesto"));
        ProductoBasicoEntity pProductoBasicoEntity = productoRepository.findById(dto.getIdProducto()).orElseThrow( () -> new IDNotFound("No existe el producto"));
//        PartidaOrdenEntity partida = PartidaOrdenValueMapper.PartidaOrdenRequestToEntity(dto,ordenEntity, pProductoEntity);
//        log.info(partida.toString());
//        return this.repository.save(partida);

//                PartidaOrdenEntity.builder()
//                .cantidad(dto.getCantidad())
//                .producto(productoRepository.findById(dto.getIdProducto()).orElseThrow(()->new RuntimeException("No existe el producto")))
//                .precioUnitario(BigDecimal.valueOf(dto.getPrecio()))
//                .build();
        return null;
    }

    @Override
    public PartidaOrdenResponseDTO getById(UUID id) {
        return null;
    }

    @Override
    public PartidaOrdenResponseDTO update(UUID id, PartidaOrdenRequestDTO dto) {
        return null;
    }

    @Override
    public PartidaOrdenResponseDTO delete(UUID id) {
        return null;
    }

    @Override
    public List<PartidaOrdenResponseDTO> getAll(Map<String, String> params) {
        return null;
    }

    public Specification<PartidaOrdenEntity> filterWithParameters(Map<String, String> params) {
        return null;
    }
}
