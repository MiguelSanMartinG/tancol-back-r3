package com.main.tancol.logistica.service;

import com.main.tancol.logistica.dto.request.ProductosFueraEspecificacionRequestDTO;
import com.main.tancol.logistica.dto.response.ProductosFueraEspecificacionResponseDTO;
import com.main.tancol.logistica.entities.bitacora.VentaTrayectoEntity;
import com.main.tancol.logistica.entities.bitacora.comisiones.ProductosFueraEspecificacionEntity;
import com.main.tancol.logistica.repositories.ProductosFueraEspecificacionRepository;
import com.main.tancol.logistica.service.interfaces.IProductosFueraEspecificacionService;
import com.main.tancol.logistica.util.ProductosFueraEspecificacionSpecification;
import com.main.tancol.logistica.util.ProductosFueraEspecificacionValueMapper;
import com.main.tancol.producto.entities.ConceptosEntity;
import com.main.tancol.producto.repositories.ConceptosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductosFueraEspecificacionService implements IProductosFueraEspecificacionService {

    private final ProductosFueraEspecificacionRepository repository;
    private final ConceptosRepository conceptosRepository;


    @Override
    public ProductosFueraEspecificacionResponseDTO create(ProductosFueraEspecificacionRequestDTO dto) {
        return null;
    }

    @Override
    public ProductosFueraEspecificacionResponseDTO getById(UUID id) {
        return null;
    }

    @Override
    public ProductosFueraEspecificacionResponseDTO update(UUID id, ProductosFueraEspecificacionRequestDTO dto) {
        return null;
    }

    @Override
    public ProductosFueraEspecificacionResponseDTO delete(UUID id) {
        return null;
    }

    @Override
    public List<ProductosFueraEspecificacionResponseDTO> getAll(Map<String, String> params) {
        Sort sort = Sort.unsorted();
        // sort = Sort.by(Sort.Direction.ASC, "createdAt");
        List<ProductosFueraEspecificacionEntity> list = repository.findAll(filterWithParameters(params), sort);
        return list.stream().map(ProductosFueraEspecificacionValueMapper::ProductosFueraEspecificacionEntityToResponse).toList();
    }

    public Specification<ProductosFueraEspecificacionEntity> filterWithParameters(Map<String, String> params) {
        return new ProductosFueraEspecificacionSpecification().getSpecificationByFilters(params);
    }

    public VentaTrayectoEntity create(VentaTrayectoEntity p, Set<ProductosFueraEspecificacionRequestDTO> productosFueraEspecificacion) {
        if(productosFueraEspecificacion == null || productosFueraEspecificacion.isEmpty()) {
            return p;
        }

        for (ProductosFueraEspecificacionRequestDTO producto : productosFueraEspecificacion) {

            ConceptosEntity concepto = conceptosRepository.findById(producto.getIdConcepto())
                    .orElseThrow(() -> new RuntimeException("No se puede encontrar un concepto"));

            p.agregarProductoConEspecificacion( ProductosFueraEspecificacionEntity.builder()
                    .cantidad(producto.getCantidad())
                    .concepto(concepto).build());
        }

        return p;
    }
}
