package com.main.tancol.almacen.service;


import com.main.tancol.almacen.dto.request.InventarioRequestDTO;
import com.main.tancol.almacen.dto.request.partidas.PartidaTransferenciaRequestDTO;
import com.main.tancol.almacen.dto.response.InventarioResponseDTO;
import com.main.tancol.almacen.entities.AlmacenEntity;
import com.main.tancol.almacen.entities.InventarioEntity;
import com.main.tancol.almacen.entities.UnidadesMedidaEntity;
import com.main.tancol.almacen.entities.partidas.PartidaTransferenciaEntity;
import com.main.tancol.almacen.repositories.AlmacenRepository;
import com.main.tancol.almacen.repositories.InventarioRepository;
import com.main.tancol.almacen.repositories.UnidadesMedidaRepository;
import com.main.tancol.almacen.service.interfaces.IInventarioService;
import com.main.tancol.almacen.util.InventarioSpecification;
import com.main.tancol.almacen.util.InventarioValueMapper;
import com.main.tancol.almacen.util.PartidaTransferenciaValueMapper;
import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import com.main.tancol.producto.repositories.ProductoRepository;
import com.main.tancol.producto.service.insumo.PrecioService;
import com.main.tancol.producto.service.insumo.StockMinimoRevisionService;
import com.main.tancol.proveedor.entity.ProveedorEntity;
import com.main.tancol.proveedor.repositories.ProveedorRepository;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventarioService implements IInventarioService {

    private final InventarioRepository inventarioRepository;
    private final AlmacenRepository almacenRepository;
    private final ProductoRepository productoRepository;
    private final UnidadesMedidaRepository unidadDeMedidaRepository;
    private final ProveedorRepository proveedorRepository;
    private final PrecioService precioService;
    private final StockMinimoRevisionService stockMinimoRevisionService;


    @Override
    @Transactional
    public InventarioResponseDTO create(InventarioRequestDTO dto) {

        Container i = initEntities(dto);
        if (i.almacen() == null || i.producto() == null) {
            throw new IDNotFound("Almacén y producto son requeridos");
        }
        Optional<InventarioEntity> checkIfExist = this.inventarioRepository.findByAlmacen_IdAndProductoBasicoEntity_Id(i.almacen().getId(), dto.getIdProducto());

        if(i.proveedor() != null){
            precioService.create(dto.getIdProducto(),i.proveedor().getId(),dto.getPrecioProveedor());
        }

        BigDecimal cantidad = cantidadValida(dto.getCantidad());

        if(checkIfExist.isPresent()) {
            InventarioEntity inventario = checkIfExist.get();
            BigDecimal actual = inventario.getCantidad() == null ? BigDecimal.ZERO : inventario.getCantidad();
            inventario.setCantidad(actual.add(cantidad));
            if (i.unidadesMedida() != null) {
                inventario.setUnidadDeMedida(i.unidadesMedida());
            }
            inventario = inventarioRepository.save(inventario);
            return InventarioValueMapper.InventarioEntityToResponse(inventario);
        }else {
        InventarioEntity newInventario = InventarioValueMapper.InventarioRequestToEntity(dto,i.producto(), i.almacen(), i.unidadesMedida());
        newInventario.setCantidad(cantidad);
        newInventario = inventarioRepository.save(newInventario);
        return InventarioValueMapper.InventarioEntityToResponse(newInventario);

        }
    }

    private  Container initEntities (InventarioRequestDTO dto) {

        AlmacenEntity almacen = null;
        ProductoBasicoEntity producto = null;
        UnidadesMedidaEntity unidadesMedidaEntity = null;
        ProveedorEntity proveedor = null;

        if(dto.getIdAlmacen() != null) almacen = this.almacenRepository.findById(dto.getIdAlmacen()).orElseThrow(() -> new IDNotFound("Almacen no encontrado"));
        if(dto.getIdProducto() != null) producto = this.productoRepository.findById(dto.getIdProducto()).orElseThrow(() -> new IDNotFound("Producto no encontrado"));
        if(dto.getIdProveedor() != null) proveedor = proveedorRepository.findById(dto.getIdProveedor()).orElseThrow(() -> new IDNotFound("Proveedor non encontrado"));
        if(dto.getUnidadDeMedida() != null) unidadesMedidaEntity = unidadDeMedidaRepository.findById(dto.getUnidadDeMedida()).orElseThrow(() -> new IDNotFound("Unidad de Medida no encontrado"));

        return Container.builder()
                .almacen(almacen)
                .producto(producto)
                .unidadesMedida(unidadesMedidaEntity)
                .proveedor(proveedor)
                .build();
    }

    @Builder
    private record Container(AlmacenEntity almacen, ProductoBasicoEntity producto, UnidadesMedidaEntity unidadesMedida, InventarioEntity inventario , ProveedorEntity proveedor) {}


    @Override
    public InventarioResponseDTO getById(UUID id) {
        InventarioEntity entity = this.inventarioRepository.findById(id).orElseThrow(() -> new IDNotFound("Inventario no encontrado"));
        return InventarioValueMapper.InventarioEntityToResponse(entity);
    }

    @Override
    public InventarioResponseDTO update(UUID id, InventarioRequestDTO dto) {
        InventarioEntity entity = this.inventarioRepository.findById(id).orElseThrow(() -> new IDNotFound("Inventario no encontrado"));
        AlmacenEntity almacen = this.almacenRepository.findById(dto.getIdAlmacen()).orElseThrow(() -> new IDNotFound("Almacen no encontrado"));
        ProductoBasicoEntity producto = this.productoRepository.findById(dto.getIdProducto()).orElseThrow(() -> new IDNotFound("Producto no encontrado"));
        BigDecimal cantidadAnterior = entity.getCantidad() == null ? BigDecimal.ZERO : entity.getCantidad();
        BigDecimal cantidadNueva = cantidadValida(dto.getCantidad());
        entity.setCantidad(cantidadNueva);
        entity.setAlmacen(almacen);
        entity.setProductoBasicoEntity(producto);
        if (dto.getUnidadDeMedida() != null) {
            UnidadesMedidaEntity unidadesMedidaEntity = this.unidadDeMedidaRepository.findById(dto.getUnidadDeMedida())
                    .orElseThrow(() -> new IDNotFound("Unidad de Medida no encontrado"));
            entity.setUnidadDeMedida(unidadesMedidaEntity);
        }

        InventarioResponseDTO response = InventarioValueMapper.InventarioEntityToResponse(this.inventarioRepository.save(entity));
        if (cantidadNueva.compareTo(cantidadAnterior) < 0) {
            stockMinimoRevisionService.revisarTrasSalida(producto.getId());
        }
        return response;

    }

    @Override
    public InventarioResponseDTO delete(UUID id) {
        InventarioEntity entity = this.inventarioRepository.findById(id).orElseThrow(() -> new IDNotFound("Inventario no encontrado"));
        InventarioResponseDTO response = InventarioValueMapper.InventarioEntityToResponse(entity);
        inventarioRepository.delete(entity);
        return response;
    }

    public Map<String,String> borrarPermanentemente(UUID id){
        InventarioEntity entity = this.inventarioRepository.findById(id).orElseThrow(() -> new IDNotFound("Inventario no encontrado"));
        inventarioRepository.delete(entity);
        Map<String,String> map = new HashMap<>();
        map.put("mensaje","Inventario eliminado con exito");
        map.put("codigo","200");
        return  map;
    }

    @Override
    public List<InventarioResponseDTO> getAll(Map<String, String> params) {
        List<InventarioEntity> list = inventarioRepository.findAll(filterWithParameters(params));
        return list.stream().map(InventarioValueMapper::InventarioEntityToResponse).toList();
    }

    public Specification<InventarioEntity> filterWithParameters(Map<String, String> params) {
        return new InventarioSpecification().getSpecificationByFilters(params);
    }

    @Transactional
    public PartidaTransferenciaEntity transferir(PartidaTransferenciaRequestDTO dto, AlmacenEntity origen, AlmacenEntity destino,
                                                 ProductoBasicoEntity productoBasico, EmpleadosEntity empleado,
                                                 UnidadesMedidaEntity uMedida) {

        if (origen.getId().equals(destino.getId())) {
            throw new IDNotFound("El almacén de origen y destino no pueden ser el mismo.");
        }

        BigDecimal cantidad = cantidadValida(dto.getCantidad());
        if (cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IDNotFound("La cantidad a transferir debe ser mayor a cero.");
        }

        InventarioEntity inventarioOrigen = this.inventarioRepository
                .findByAlmacen_IdAndProductoBasicoEntity_Id(origen.getId(), productoBasico.getId())
                .orElseThrow(() -> new IDNotFound("No se encontró el producto en el almacén de origen."));
        Optional<InventarioEntity> inventarioDestino = this.inventarioRepository
                .findByAlmacen_IdAndProductoBasicoEntity_Id(destino.getId(), productoBasico.getId());

        BigDecimal existenciaOrigen = inventarioOrigen.getCantidad() == null ? BigDecimal.ZERO : inventarioOrigen.getCantidad();
        if(existenciaOrigen.compareTo(cantidad) < 0) {
            throw new IDNotFound("No se puede transferir una cantidad mayor a la que hay en existencia.");
        }

        //Si hay un inventario con producto en el almacén destino.
        if(inventarioDestino.isPresent()) {
            InventarioEntity destinoEntity = inventarioDestino.get();
            BigDecimal existenciaDestino = destinoEntity.getCantidad() == null ? BigDecimal.ZERO : destinoEntity.getCantidad();
            destinoEntity.setCantidad(existenciaDestino.add(cantidad));
            this.inventarioRepository.save(destinoEntity);
            log.info("Se transfirio, se ignoro la unidad de medida origen.");
        }
        else {
            InventarioEntity newInventario = InventarioEntity.builder()
                    .almacen(destino)
                    .productoBasicoEntity(productoBasico)
                    .cantidad(cantidad)
                    .unidadDeMedida(uMedida != null ? uMedida : inventarioOrigen.getUnidadDeMedida())
                    .build();
            this.inventarioRepository.save(newInventario);
        }

        inventarioOrigen.setCantidad(existenciaOrigen.subtract(cantidad));
        this.inventarioRepository.save(inventarioOrigen);
        stockMinimoRevisionService.revisarTrasSalida(productoBasico.getId());
        log.info("Se transfirio de forma existosa.");
        return PartidaTransferenciaValueMapper.PartidaTransferenciaRequestToEntity(dto, origen, destino, productoBasico, uMedida, empleado);

    }

    private BigDecimal cantidadValida(BigDecimal cantidad) {
        if (cantidad == null) {
            throw new IDNotFound("La cantidad es requerida.");
        }
        if (cantidad.compareTo(BigDecimal.ZERO) < 0) {
            throw new IDNotFound("La cantidad no puede ser negativa.");
        }
        return cantidad;
    }



}
