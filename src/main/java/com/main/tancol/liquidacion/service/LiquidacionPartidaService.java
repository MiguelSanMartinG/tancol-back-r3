package com.main.tancol.liquidacion.service;

import com.main.tancol.cliente.entities.ClienteEntity;
import com.main.tancol.cliente.repositories.ClienteRepository;
import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.liquidacion.dto.request.LiquidacionPartidaRequestDTO;
import com.main.tancol.liquidacion.dto.response.LiquidacionPartidaResponseDTO;
import com.main.tancol.liquidacion.entities.*;
import com.main.tancol.liquidacion.enums.EstadosLiquidacion;
import com.main.tancol.liquidacion.repositories.ConceptosVentasRepository;
import com.main.tancol.liquidacion.repositories.LiquidacionPartidaRepository;
import com.main.tancol.liquidacion.service.interfaces.ILiquidacionPartidaService;
import com.main.tancol.liquidacion.util.LiquidacionPartidaSpecification;
import com.main.tancol.liquidacion.util.LiquidacionPartidaValueMapper;
import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
import com.main.tancol.producto.repositories.ConceptosRepository;
import com.main.tancol.producto.repositories.ProductoComercialRepository;
import com.main.tancol.producto.repositories.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LiquidacionPartidaService implements ILiquidacionPartidaService {

    private final LiquidacionPartidaRepository repository;
    private final ConceptosRepository conceptosRepository;
    private final ConceptosVentasRepository conceptosVentasRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final ProductoComercialRepository productoComercialRepository;
    private final ReparticionEntreCuentasService reparticionEntreCuentasService;


    @Override
    public LiquidacionPartidaResponseDTO create(LiquidacionPartidaRequestDTO dto) {
        return null;
    }

    @Override
    public LiquidacionPartidaResponseDTO getById(UUID id) {
        return null;
    }

    @Override
    public LiquidacionPartidaResponseDTO update(UUID id, LiquidacionPartidaRequestDTO dto) {
        return null;
    }

    @Override
    public LiquidacionPartidaResponseDTO delete(UUID id) {
        return null;
    }

    @Override
    public List<LiquidacionPartidaResponseDTO> getAll(Map<String, String> params) {
        Sort sort = null;
        // sort = Sort.by(Sort.Direction.ASC, "createdAt");
        List<LiquidacionPartidaEntity> list = repository.findAll(filterWithParameters(params), sort);
        return list.stream().map(LiquidacionPartidaValueMapper::LiquidacionPartidaEntityToResponse).toList();
    }

    public Specification<LiquidacionPartidaEntity> filterWithParameters(Map<String, String> params) {
        return new LiquidacionPartidaSpecification().getSpecificationByFilters(params);
    }

    public void create(LiquidacionEntity entity, List<LiquidacionPartidaRequestDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) return;
        log.info("Creando Partidas:");
        try{
            for (LiquidacionPartidaRequestDTO partidaDto : dtos) {


                ConceptosVentasEntity concepto = null;
                LiquidacionDistribucionDescuentoEntity reparto = null;
                if (partidaDto.getIdConceptoVenta() != null) {
                    concepto = conceptosVentasRepository.findById(partidaDto.getIdConceptoVenta()).orElseThrow(() -> new IDNotFound("No se encontró el concepto"));
                }
                ClienteEntity cliente = null;
                if (partidaDto.getIdCliente() != null) {
                    cliente = clienteRepository.findById(partidaDto.getIdCliente()).orElseThrow(() -> new IDNotFound("No se encontró el cliente"));
                }
                ProductoComercialEntity producto = null;
                if (partidaDto.getIdProducto() != null) {
                    producto = productoComercialRepository.findById(partidaDto.getIdProducto()).orElseThrow(() -> new IDNotFound("No se encontró el producto"));
                }

                if(!partidaDto.getDescuento().equals(BigDecimal.ZERO)& entity.getEstadosLiquidacion().equals(EstadosLiquidacion.FINALIZADA)){
                    ReparticionEntreCuentasEntity c = reparticionEntreCuentasService.getLatest();
                    BigDecimal total = c.getCuentaA().add(c.getCuentaB()).add(c.getCuentaC());
                    BigDecimal descuento = partidaDto.getDescuento();

                    BigDecimal a = c.getCuentaA();
                    BigDecimal b = c.getCuentaB();
                    BigDecimal remanente = descuento.subtract(a).subtract(b);

                    if(total.compareTo(partidaDto.getDescuento()) > 0) {
                        throw new IDNotFound("El descuento es menor al descuento minimo de la tabla");
                    }

                    if(ObjectUtils.isEmpty(partidaDto.getCantidad())){
                        throw new IDNotFound("No se tiene la cantidad");
                    }

                    reparto = LiquidacionDistribucionDescuentoEntity.builder()
                            .cuentaA(a.multiply(partidaDto.getCantidad()))
                            .cuentaB(b.multiply(partidaDto.getCantidad()))
                            .cuentaC(remanente.multiply(partidaDto.getCantidad()))
                            .build();
                }

                LiquidacionPartidaEntity p = formarEntidad(entity, partidaDto, concepto, cliente, producto, reparto);

                //Se origen al la distribucion.
                if ((ObjectUtils.isNotEmpty(p.getDistribucionDescuento()))) {
                    p.getDistribucionDescuento().setLiquidacionPartidaEntity(p);
                }

                entity.agregarNota(p);
            }
        }catch (Exception e){
            log.error("Error inesperado al crear liquidación", e);
            throw new RuntimeException("Error al procesar la creación de la liquidación", e);
        }

    }

    private LiquidacionPartidaEntity formarEntidad(LiquidacionEntity entity, LiquidacionPartidaRequestDTO partidaDto, ConceptosVentasEntity concepto, ClienteEntity cliente, ProductoComercialEntity producto, LiquidacionDistribucionDescuentoEntity reparto) {
        return LiquidacionPartidaEntity.builder()
                .folio(partidaDto.getFolio())
                .conceptosVentas(concepto)
                .cliente(cliente)
                .productoComercial(producto)
                .cantidad(ObjectUtils.isNotEmpty(partidaDto.getCantidad()) ? partidaDto.getCantidad() : BigDecimal.ZERO)
                .descuento(ObjectUtils.isNotEmpty(partidaDto.getDescuento()) ? partidaDto.getDescuento() : BigDecimal.ZERO)
                .precio(ObjectUtils.isNotEmpty(partidaDto.getPrecio()) ? partidaDto.getPrecio() : BigDecimal.ZERO)
                .factura(partidaDto.getFactura())
                .formaPago(partidaDto.getFormaPago())
                .liquidacionOrigen(entity)
                .distribucionDescuento(reparto)
                .build();
    }

    public void update(LiquidacionEntity liquidacion, List<LiquidacionPartidaRequestDTO> dto) {
        if (dto == null || dto.isEmpty()) {
            liquidacion.clearNotas();
            return;
        }

        List<LiquidacionPartidaRequestDTO> crear = new ArrayList<>();
        List<LiquidacionPartidaRequestDTO> actualizar = new ArrayList<>();
        List<UUID> conjuntoConId = new ArrayList<>();

        for (LiquidacionPartidaRequestDTO partidaDto : dto) {
            if (partidaDto.getIdPartida() == null) {
                crear.add(partidaDto); // Las que se crearan
            } else {
                conjuntoConId.add(partidaDto.getIdPartida()); // Las que id que persistiran.
                actualizar.add(partidaDto); // las partidas que percistiran.
            }
        }

        quitarSobrantes(liquidacion, conjuntoConId);
        actualizar(liquidacion, actualizar);
        create(liquidacion, crear);

    }
    private void actualizar(LiquidacionEntity liquidacion, List<LiquidacionPartidaRequestDTO> actualizar) {
        if (actualizar.isEmpty()) return;
        log.info("Actualizando Partidas");
        Map<UUID, LiquidacionPartidaEntity> existencias = liquidacion.getLiquidacionPartida().stream()
                .collect(Collectors.toMap(LiquidacionPartidaEntity::getIdPartida, Function.identity()));
        log.info(existencias.toString());
        for (LiquidacionPartidaRequestDTO updateDTO : actualizar) {

            LiquidacionPartidaEntity update = existencias.get(updateDTO.getIdPartida());
            if (update != null){
                ProductoComercialEntity pc = null;
                if (updateDTO.getIdProducto() != null) {
                    pc = productoComercialRepository.findById(updateDTO.getIdProducto()).orElseThrow(() -> new IDNotFound("No se encontr<UNK> el producto"));
                }
                ClienteEntity c = null;
                if (updateDTO.getIdCliente() != null) {
                    c = clienteRepository.findById(updateDTO.getIdCliente()).orElseThrow(() -> new IDNotFound("No se encontr<UNK> el producto"));
                }
                ConceptosVentasEntity cv = null;
                if (updateDTO.getIdConceptoVenta() != null) {
                    cv = conceptosVentasRepository.findById(updateDTO.getIdConceptoVenta()).orElseThrow(() -> new IDNotFound("No se encontr<UNK> el Convepto Venta"));
                }

                update.setCantidad(updateDTO.getCantidad());
                update.setFolio(updateDTO.getFolio());
                update.setPrecio(updateDTO.getPrecio());
                update.setDescuento(updateDTO.getDescuento());
                update.setFormaPago(updateDTO.getFormaPago());
                update.setConceptosVentas(cv);
                update.setProductoComercial(pc);
                update.setCliente(c);
                update.setFactura(updateDTO.getFactura());
                update.setFolio(updateDTO.getFolio());
                log.info(update.toString());

            }else{
                throw new IDNotFound("El idPartida es incorrecto: "+updateDTO.getIdPartida());
            }
        }
    }

    private void quitarSobrantes(LiquidacionEntity liquidacion, List<UUID> conjuntoConId) {
//        log.info("Removiendo partidas:");
//        liquidacion.getLiquidacionPartida().removeIf(x ->
//                !conjuntoConId.contains(x.getIdPartida())
//        );


        log.info("Removiendo partidas:");
        // Usamos Iterator para evitar ConcurrentModificationException
        Iterator<LiquidacionPartidaEntity> iterator = liquidacion.getLiquidacionPartida().iterator();
        while (iterator.hasNext()) {
            LiquidacionPartidaEntity p = iterator.next();
            if (!conjuntoConId.contains(p.getIdPartida())) {
                log.info("Nota " + p.getIdPartida() + " borrada.");
                liquidacion.removerNota(p); // Esto ya remueve de la colección internamente
            }
        }
        // Opcional: Limpia la lista para forzar la recarga desde la base de datos si es necesario
        // liquidacion.getLiquidacionPartida().clear();
        // Esto es útil si la entidad está gestionada por JPA y planeas guardarla
        // para forzar una operación de sincronización.
        // Pero generalmente, si la relación está bien mapeada con cascade REMOVE o orphanRemoval,
        // marcar la entidad como eliminada (posiblemente seteando un estado o dejándola sin padre)
        // y luego guardar la entidad padre (liquidacion) debería propagar los cambios.
    }

}

