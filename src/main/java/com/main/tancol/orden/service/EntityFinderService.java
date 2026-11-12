package com.main.tancol.orden.service;

import com.main.tancol.almacen.entities.AlmacenEntity;
import com.main.tancol.almacen.repositories.AlmacenRepository;
import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.orden.entities.PartidaOrdenEntity;
import com.main.tancol.orden.repositories.OrdenRepository;
import com.main.tancol.orden.repositories.PartidaOrdenRepository;
import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import com.main.tancol.producto.repositories.PrecioRepository;
import com.main.tancol.producto.repositories.ProductoRepository;
import com.main.tancol.proveedor.entity.ProveedorEntity;
import com.main.tancol.proveedor.entity.TipoImpuestoEntity;
import com.main.tancol.proveedor.repositories.ImpuestosRepository;
import com.main.tancol.proveedor.repositories.ProveedorRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EntityFinderService {
    private final OrdenRepository repository;
    private final PartidaOrdenRepository partidaOrdenRepository;
    private final ProveedorRepository proveedorRepository;
    private final ImpuestosRepository impuestosRepository;
    private final ProductoRepository productoRepository;
    private final AlmacenRepository almacenRepository;
    private final PrecioRepository precioRepository;


    public EntityFinderService(OrdenRepository repository, PartidaOrdenRepository partidaOrdenRepository, ProveedorRepository proveedorRepository, ImpuestosRepository impuestosRepository, ProductoRepository productoRepository, AlmacenRepository almacenRepository, PrecioRepository precioRepository) {
        this.repository = repository;
        this.partidaOrdenRepository = partidaOrdenRepository;
        this.proveedorRepository = proveedorRepository;
        this.impuestosRepository = impuestosRepository;
        this.productoRepository = productoRepository;
        this.almacenRepository = almacenRepository;
        this.precioRepository = precioRepository;
    }

    public ProveedorEntity findProveedor(UUID id) throws IDNotFound {
        return this.proveedorRepository.findById(id)
                .orElseThrow(() -> new IDNotFound("Id no encontrada del producto"));
    }
    public ProductoBasicoEntity findProducto(UUID id) throws IDNotFound {
        return this.productoRepository.findById(id)
                .orElseThrow(() -> new IDNotFound("Id del proveedor no encontrada"));
    }

    public TipoImpuestoEntity findTipoImpuesto(UUID id) throws IDNotFound {
        return impuestosRepository.findById(id)
                .orElseThrow(() -> new IDNotFound("La id del impuesto no existe"));
    }

    public PartidaOrdenEntity findByPartida(UUID id) throws IDNotFound {
        return partidaOrdenRepository.findById(id).orElseThrow( () -> new IDNotFound("Id no encontrada del partida"));
    }
    public AlmacenEntity findByAlmacen(UUID id, int index) throws IDNotFound {
        return almacenRepository.findById(id).orElseThrow( () -> new IDNotFound("Id no encontrada del almacen" + index));
    }

//    public PrecioProveedorEntity findPrecioProducto(ProveedorEntity proveedorQuery, ProductoEntity productoQuery) {
//        return precioRepository.findFirstByProductoEntity_IdAndProveedorEntity_Id(proveedorQuery.getId(), productoQuery.getId()).orElseGet(
//            PrecioProveedorEntity pr = PrecioProveedorEntity.builder().proveedorEntity(proveedorQuery).productoEntity(productoQuery).precio(new BigDecimal(0)).build();
//            return pr;
//        );
//    }
}
