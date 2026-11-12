package com.main.tancol.almacen.service;

import com.main.tancol.almacen.dto.request.partidas.PartidaOrdenInternaRequestDTO;
import com.main.tancol.almacen.dto.response.partidas.PartidaOrdenInternaResponseDTO;
import com.main.tancol.almacen.entities.AlmacenEntity;
import com.main.tancol.almacen.entities.InventarioEntity;
import com.main.tancol.almacen.entities.OrdenInternaEntity;
import com.main.tancol.almacen.entities.UnidadesMedidaEntity;
import com.main.tancol.almacen.repositories.AlmacenRepository;
import com.main.tancol.almacen.repositories.InventarioRepository;
import com.main.tancol.almacen.repositories.PartidaOrdenInternaRepository;
import com.main.tancol.almacen.repositories.UnidadesMedidaRepository;
import com.main.tancol.almacen.util.PartidaOrdenInternaValueMapper;
import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import com.main.tancol.producto.repositories.ProductoRepository;
import com.main.tancol.producto.service.insumo.StockMinimoRevisionService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import com.main.tancol.almacen.entities.partidas.PartidaOrdenInternaEntity;
import com.main.tancol.almacen.util.PartidaOrdenInternaSpecification;
import com.main.tancol.almacen.service.interfaces.IPartidaOrdenInternaService;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
public class PartidaOrdenInternaService implements IPartidaOrdenInternaService {

    private final PartidaOrdenInternaRepository repository;
    private final AlmacenRepository almacenRepository;
    private final ProductoRepository productoBasicoRepository;
    private final UnidadesMedidaRepository unidadesMedidaService;
    private final InventarioRepository inventarioRepository;
    private final StockMinimoRevisionService stockMinimoRevisionService;

    public PartidaOrdenInternaService(PartidaOrdenInternaRepository repository, AlmacenRepository almacenRepository, ProductoRepository productoBasicoRepository, UnidadesMedidaRepository unidadesMedidaService, InventarioRepository inventarioRepository, StockMinimoRevisionService stockMinimoRevisionService) {
        this.repository = repository;
        this.almacenRepository = almacenRepository;
        this.productoBasicoRepository = productoBasicoRepository;
        this.unidadesMedidaService = unidadesMedidaService;
        this.inventarioRepository = inventarioRepository;
        this.stockMinimoRevisionService = stockMinimoRevisionService;
    }


    @Override
    public PartidaOrdenInternaResponseDTO create(PartidaOrdenInternaRequestDTO dto) {

//        PartidaOrdenInternaEntity partidaOrdenInternaEntity = PartidaOrdenInternaEntity.builder().ordenInternaEntity().build();


//        OrdenInternaEntity orden = OrdenInternaValueMapper.OrdenInternaRequestToEntity(dto);
        return null;
    }

    @Override
    public PartidaOrdenInternaResponseDTO getById(UUID id) {
        return null;
    }

    @Override
    public PartidaOrdenInternaResponseDTO update(UUID id, PartidaOrdenInternaRequestDTO dto) {
        return null;
    }

    @Override
    @Transactional
    public PartidaOrdenInternaResponseDTO delete(UUID id) {
        PartidaOrdenInternaEntity partida = repository.findById(id)
                .orElseThrow(() -> new IDNotFound("No se encontró la partida"));

        AlmacenEntity almacen = partida.getAlmacenEntity();
        ProductoBasicoEntity producto = partida.getProductoBasicoEntity();
        if (almacen == null || almacen.getId() == null || producto == null || producto.getId() == null) {
            throw new IDNotFound("La partida no tiene almacén o producto");
        }

        InventarioEntity inventario = inventarioRepository
                .findByAlmacen_IdAndProductoBasicoEntity_Id(almacen.getId(), producto.getId())
                .orElseThrow(() -> new IDNotFound("No se encontró el producto en el almacén"));

        BigDecimal cantidad = partida.getCantidad() == null ? BigDecimal.ZERO : partida.getCantidad();
        BigDecimal existencia = inventario.getCantidad() == null ? BigDecimal.ZERO : inventario.getCantidad();
        inventario.setCantidad(existencia.add(cantidad));
        inventarioRepository.save(inventario);

        PartidaOrdenInternaResponseDTO response = PartidaOrdenInternaValueMapper.PartidaOrdenInternaEntityToResponse(partida);

        OrdenInternaEntity orden = partida.getOrdenInternaEntity();
        if (orden != null && orden.getPartidas() != null) {
            orden.getPartidas().remove(partida);
        }
        repository.delete(partida);
        stockMinimoRevisionService.revisarTrasDevolucion(producto.getId());
        return response;
    }

    @Override
    public List<PartidaOrdenInternaResponseDTO> getAll(Map<String, String> params) {
        List<PartidaOrdenInternaEntity> list = repository.findAll(filterWithParameters(params));
        return list.stream().map(PartidaOrdenInternaValueMapper::PartidaOrdenInternaEntityToResponse).toList();
    }

    public Specification<PartidaOrdenInternaEntity> filterWithParameters(Map<String, String> params) {
        return new PartidaOrdenInternaSpecification().getSpecificationByFilters(params);
    }

    @Transactional
    public List<PartidaOrdenInternaEntity> createListPartida(List<PartidaOrdenInternaRequestDTO> dto, OrdenInternaEntity ordenInterna) {

        Set<UUID> productosSalidos = new HashSet<>();
        List<PartidaOrdenInternaEntity> list = dto.stream().map((partida) -> {
            log.info(partida.toString());
            AlmacenEntity almacenEntity = this.almacenRepository.findById(partida.getIdAlmacen()).orElseThrow( () -> new IDNotFound("No se encontro el almacen"));
            ProductoBasicoEntity productoBasicoEntity = this.productoBasicoRepository.findById(partida.getIdProductoBasico()).orElseThrow( () -> new IDNotFound("No se encontro el producto basico"));
            UnidadesMedidaEntity unidadesMedidaEntity = partida.getUnidadMedida() == null ? null :
                    this.unidadesMedidaService.findById(partida.getUnidadMedida()).orElseThrow(()-> new IDNotFound("No se encontro la unidad de medida"));
            InventarioEntity inventario = this.inventarioRepository.findByAlmacen_IdAndProductoBasicoEntity_Id(almacenEntity.getId(), productoBasicoEntity.getId()).orElseThrow( () -> new IDNotFound("No se encontro producto en el almacen solicitado"));
            BigDecimal cantidad = partida.getCantidad();
            if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IDNotFound("La cantidad a solicitar debe ser mayor a cero");
            }
            BigDecimal existencia = inventario.getCantidad() == null ? BigDecimal.ZERO : inventario.getCantidad();
            if (existencia.compareTo(cantidad) < 0) {
                throw new IDNotFound("No se puede realizar la solicitud el almacén no tiene suficiente producto");
            }
            inventario.setCantidad(existencia.subtract(cantidad));
            this.inventarioRepository.save(inventario);
            productosSalidos.add(productoBasicoEntity.getId());
            return PartidaOrdenInternaValueMapper.PartidaOrdenInternaRequestToEntity(partida, ordenInterna, almacenEntity,unidadesMedidaEntity,productoBasicoEntity);
        }).toList();

        list = (List<PartidaOrdenInternaEntity>) this.repository.saveAll(list);
        productosSalidos.forEach(stockMinimoRevisionService::revisarTrasSalida);
        return list;

    }
}
