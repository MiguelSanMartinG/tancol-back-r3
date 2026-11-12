package com.main.tancol.producto.service;

import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.producto.dto.request.PrecioRequestDTO;
import com.main.tancol.producto.dto.response.PrecioResponseDTO;
import com.main.tancol.producto.entities.ProductoEntity;
import com.main.tancol.producto.entities.PrecioEntity;
import com.main.tancol.producto.repositories.ProductoRepository;
import com.main.tancol.producto.repositories.PrecioRepository;
import com.main.tancol.producto.service.interfaces.IPrecioService;
import com.main.tancol.producto.util.ProductoValueMapper;
import com.main.tancol.proveedor.entity.ProveedorEntity;
import com.main.tancol.proveedor.repositories.ProveedorRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class PrecioService implements IPrecioService {
    private final PrecioRepository repository;
    private final ProductoRepository pRepository;
    private final ProveedorRepository proRepository;

    public PrecioService(PrecioRepository precioProveedor, ProductoRepository pRepository, ProveedorRepository proRepository) {
        this.repository = precioProveedor;
        this.pRepository = pRepository;
        this.proRepository = proRepository;
    }


    @Override
    public PrecioResponseDTO getPrecio(UUID productoId, UUID proveedorId) {


        ProductoEntity productoEntity = pRepository.findById(productoId).orElseThrow(() -> new IDNotFound("No se encontro el producto"));
        ProveedorEntity proveedorEntity = proRepository.findById(proveedorId).orElseThrow(() -> new IDNotFound("No se encontro el proveedor"));

        PrecioEntity response = repository.findByProductoEntity_IdAndProveedorEntity_Id(productoId, proveedorId).orElseGet(() -> {

                    PrecioEntity newPrecio = PrecioEntity.builder()
                            .productoEntity(productoEntity)
                            .proveedorEntity(proveedorEntity)
                            .precio(new BigDecimal(0))
                            .build();
                    return this.repository.save(newPrecio);
                }
        );
        return ProductoValueMapper.PrecioEntityToResponse(response);
    }

    @Override
    @Transactional
    public PrecioResponseDTO setPrecio(PrecioRequestDTO dto) {
        PrecioEntity query = repository.findById(dto.getIdPrecio()).orElseThrow(() -> new IDNotFound("No se encontro el precio"));
        query.setPrecio(dto.getPrecio());
        return ProductoValueMapper.PrecioEntityToResponse(repository.save(query));
    }

    @Override
    public List<PrecioResponseDTO> findAll() {
        List<PrecioEntity> lista = (List<PrecioEntity>) repository.findAll();
        return lista.stream().map(ProductoValueMapper::PrecioEntityToResponse).toList();
    }


}
