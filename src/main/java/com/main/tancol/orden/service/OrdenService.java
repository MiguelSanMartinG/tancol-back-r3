package com.main.tancol.orden.service;

import com.main.tancol.almacen.entities.AlmacenEntity;
import com.main.tancol.common.dto.CatalogPage;
import com.main.tancol.common.dto.PageResponse;
import com.main.tancol.almacen.entities.InventarioEntity;
import com.main.tancol.almacen.entities.UnidadesMedidaEntity;
import com.main.tancol.almacen.repositories.AlmacenRepository;
import com.main.tancol.almacen.repositories.InventarioRepository;
import com.main.tancol.almacen.repositories.UnidadesMedidaRepository;
import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.common.service.FolioService;
import com.main.tancol.orden.dto.request.FacturacionOrdenRequestDTO;
import com.main.tancol.orden.dto.request.PartidaOrdenRequestDTO;
import com.main.tancol.orden.dto.request.RecepcionOrdenRequestDTO;
import com.main.tancol.orden.dto.request.SolicitudOrdenRequestDTO;
import com.main.tancol.orden.dto.response.FacturacionOrdenResponseDTO;
import com.main.tancol.orden.dto.response.RecepcionOrdenResponseDTO;
import com.main.tancol.orden.dto.response.SolicitudOrdenResponseDTO;
import com.main.tancol.orden.entities.OrdenEntity;
import com.main.tancol.orden.entities.PartidaOrdenEntity;
import com.main.tancol.orden.enums.FacturacionOrdenEstatus;
import com.main.tancol.orden.enums.Prioridad;
import com.main.tancol.orden.enums.RecepcionEstatus;
import com.main.tancol.orden.enums.SolicitudEstatus;
import com.main.tancol.orden.repositories.OrdenRepository;
import com.main.tancol.orden.repositories.PartidaOrdenRepository;
import com.main.tancol.orden.service.interfaces.IOrdenService;
import com.main.tancol.orden.util.OrdenPageSpecification;
import com.main.tancol.orden.util.OrdenSpecification;
import com.main.tancol.orden.util.OrdenValueMapper;
import com.main.tancol.orden.util.PartidaOrdenValueMapper;
import com.main.tancol.producto.entities.PresentacionProductoBasico;
import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import com.main.tancol.producto.repositories.PrecioRepository;
import com.main.tancol.producto.service.insumo.StockMinimoRevisionService;
import com.main.tancol.proveedor.entity.ProveedorEntity;
import com.main.tancol.proveedor.entity.TipoImpuestoEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrdenService implements IOrdenService {

    private final OrdenRepository ordenRepository;
    private final PartidaOrdenRepository partidaOrdenRepository;
    private final AlmacenRepository almacenRepository;
    private final JdbcTemplate jdbcTemplate;
    private final PrecioRepository precioRepository;
    private final EntityFinderService entityFinderService;
    private final InventarioRepository inventarioRepository;
    private final FolioService folioService;
    private final UnidadesMedidaRepository unidadesMedidaRepository;
    private final StockMinimoRevisionService stockMinimoRevisionService;


    private BigDecimal calcularTotalPartida(
            PartidaOrdenRequestDTO row,
            TipoImpuestoEntity tipoImpuestoEntity,
            PresentacionProductoBasico precio) {
        // TOTAL = (PU * CANTIDAD) + ((PU*CAN)*IMPUESTO/100) - DESCUENTO
        BigDecimal pu = precio.getPrecio();
        BigDecimal cantidad = BigDecimal.valueOf(row.getCantidad());
        BigDecimal impuesto = tipoImpuestoEntity.getPorcentaje().divide(new BigDecimal(100), RoundingMode.HALF_UP);
        BigDecimal descuento = row.getDescuentoAplicado();
        BigDecimal precioPorCantidad = pu.multiply(cantidad);
        BigDecimal totalPartida = (precioPorCantidad.add(precioPorCantidad.multiply(impuesto)));
        return totalPartida.subtract(descuento);
    }


    //! LOGICA DE SOLICITUD DE ORDEN DE COMPRA //
    @Override
    @Transactional
    public SolicitudOrdenResponseDTO create(SolicitudOrdenRequestDTO dto) {

        ProveedorEntity proveedor = null;
        if(dto.getProveedorId() != null){
         proveedor = entityFinderService.findProveedor(dto.getProveedorId());
        }
        
        String folio = folioService.generarFolio(1);

        OrdenEntity ordenEntity = OrdenValueMapper.SolicitudRequestDTOToEntity(dto, folio, proveedor);
        OrdenEntity createdOrden = this.ordenRepository.save(ordenEntity);



        AtomicReference<BigDecimal> acumuladorTotalOrden = new AtomicReference<>(new BigDecimal("0.0"));
        List<PartidaOrdenEntity> partidas = dto.getPartidas().stream()
                .filter(row -> {
                    boolean esValida = row.getIdImpuesto() != null && row.getIdProducto() != null && dto.getProveedorId() != null;
                    if (!esValida) {
                        log.info("Se detectó una partida incompleta. Esta no se procesará.");
                    }
                    return esValida;
                })
                .map(row -> {
                    // Validacion si existen esos productos...

                    UnidadesMedidaEntity u = null;
                    if(row.getIdUnidadMedida() != null){
                        u = unidadesMedidaRepository.findById(row.getIdUnidadMedida()).orElseThrow(() -> new IDNotFound("No se encontro la unidad de medida de una partida"));
                    }else {
                        u = unidadesMedidaRepository.findFirstByAlias("Pz");
                    }


                    TipoImpuestoEntity tipoImpuestoQuery = entityFinderService.findTipoImpuesto(row.getIdImpuesto());
                    ProductoBasicoEntity productoQuery = entityFinderService.findProducto(row.getIdProducto());

                    PresentacionProductoBasico precioQuery = precioRepository.findByProductoBasicoEntity_IdAndProveedorEntity_Id(
                            productoQuery.getId(), dto.getProveedorId())
                            .orElseThrow(() -> new IDNotFound("El la presentación del producto cuenta con un error"));


                    BigDecimal totalPartida = calcularTotalPartida(row, tipoImpuestoQuery, precioQuery);
                    //Acumula el total de la partida
                    acumuladorTotalOrden.updateAndGet(currentTotal -> currentTotal.add(totalPartida));
                    // Crear la partida y asociarla a la orden  )
                    PartidaOrdenEntity rowPartida = this.partidaOrdenRepository.save(PartidaOrdenValueMapper.
                            PartidaOrdenRequestToEntity(row, createdOrden, productoQuery, precioQuery, tipoImpuestoQuery, u));
                    return rowPartida;
                })
                .collect(Collectors.toList()); // Recolectar las partidas en una lista

//        List<PartidaOrdenEntity> partidasGuardadas = (List<PartidaOrdenEntity>) this.partidaOrdenRepository.saveAll(partidas);

        if (proveedor != null) {
        createdOrden.setPartidas(partidas);
        createdOrden.setGetFechaCreacionSolicitud(LocalDateTime.now());
        }
        createdOrden.setTotalOrden(acumuladorTotalOrden.get());

        OrdenEntity orden = ordenRepository.save(createdOrden);

        return OrdenValueMapper.OrdenEntityToResponseDTO(orden, partidas);
    }

    @Override
    @Transactional
    public SolicitudOrdenResponseDTO update(UUID id, SolicitudOrdenRequestDTO dto) {
        OrdenEntity query = findById(id);
        ProveedorEntity proveedor = null;
        if(dto.getProveedorId() != null){
            proveedor = entityFinderService.findProveedor(dto.getProveedorId());
        }

        OrdenEntity updatedOrder = OrdenValueMapper.SolicitudRequestDTOToEntity(dto, query.getFolio(), proveedor);
        updatedOrder.setId(query.getId());

        AtomicReference<BigDecimal> acumuladorTotalOrden = new AtomicReference<>(new BigDecimal("0.0"));
        List<PartidaOrdenEntity> partidas = dto.getPartidas().stream()
                .filter(row -> {
                    boolean esValida = row.getIdImpuesto() != null && row.getIdProducto() != null && dto.getProveedorId() != null;
                    if (!esValida) {
                        log.info("Se detectó una partida incompleta. Esta no se procesará.");
                    }
                    return esValida;
                })
                .map((row) -> {
                    ProveedorEntity proveedorRow = entityFinderService.findProveedor(dto.getProveedorId());
                    TipoImpuestoEntity tipoImpuestoQuery = entityFinderService.findTipoImpuesto(row.getIdImpuesto());
                    ProductoBasicoEntity producto = entityFinderService.findProducto(row.getIdProducto());
                    //La presentacion es la relaccion - presio producto
                    PresentacionProductoBasico presentacionProducto = buscarOCrearRelaccion(row, proveedorRow, producto);
                    BigDecimal totalPartida = calcularTotalPartida(row, tipoImpuestoQuery, presentacionProducto);
                    acumuladorTotalOrden.updateAndGet(currentTotal -> currentTotal.add(totalPartida));

                    UnidadesMedidaEntity u = null;
                    if(row.getIdUnidadMedida() != null){
                        u = unidadesMedidaRepository.findById(row.getIdUnidadMedida()).orElseThrow(() -> new IDNotFound("No se encontro el id UUID"));
                    }else {
                        u = unidadesMedidaRepository.findFirstByAlias("Pz");
                    }

                    if(row.getIdPartida() != null){
                        PartidaOrdenEntity partida = partidaOrdenRepository.findById(row.getIdPartida())
                                .orElseThrow(() -> new IDNotFound("Una partida proporcionada no se encuentro"));
                        PartidaOrdenEntity savedPartida = partidaOrdenRepository.save(
                                PartidaOrdenValueMapper.SavePartidaOrden(partida, updatedOrder, producto, presentacionProducto, tipoImpuestoQuery));
                        return this.partidaOrdenRepository.save(savedPartida);
                    }else{
                        PartidaOrdenEntity createdOrden = PartidaOrdenValueMapper.PartidaOrdenRequestToEntity(row,updatedOrder ,producto, presentacionProducto, tipoImpuestoQuery, u);
                        return this.partidaOrdenRepository.save(createdOrden);
                    }

                })
                .toList(); // Recolectar las partidas en una lista

            updatedOrder.getPartidas().clear();
            updatedOrder.getPartidas().addAll(partidas);
            updatedOrder.setTotalOrden(acumuladorTotalOrden.get());
            updatedOrder.setUpdatedAt(LocalDateTime.now());
            OrdenEntity dbOrden = this.ordenRepository.save(updatedOrder);
        return OrdenValueMapper.OrdenEntityToResponseDTO(dbOrden, dbOrden.getPartidas());
    }

    @Override
    public SolicitudOrdenResponseDTO delete(UUID id) {
        OrdenEntity query = findById(id);
        query.setDeleted(true);
        query = ordenRepository.save(query);
        return OrdenValueMapper.OrdenEntityToResponseDTO(query, query.getPartidas());
    }

    @Override
    public SolicitudOrdenResponseDTO getById(UUID id) {
        OrdenEntity query = findById(id);
        return OrdenValueMapper.OrdenEntityToResponseDTO(query, query.getPartidas());
    }

    @Override
    public List<SolicitudOrdenResponseDTO> getAll(Map<String, String> params) {
        Sort sort = Sort.by(Sort.Direction.ASC, "folio"); // o el campo que necesites
        List<OrdenEntity> list = ordenRepository.findAll(filterWithParameters(params), sort);
        return list.stream().map((x) -> OrdenValueMapper.OrdenEntityToResponseDTO(x, x.getPartidas())).toList();
    }

    private static final java.util.Set<String> SOLICITUD_FILTERS = java.util.Set.of(
            "folio", "proveedor", "totalOrden", "prioridad", "fechaSolicitud", "fechaModificacion", "solicitudEstatus"
    );
    private static final java.util.Map<String, String> SOLICITUD_SORTS = java.util.Map.of(
            "folio", "folio",
            "proveedor", "proveedor.nombreComercial",
            "totalOrden", "totalOrden",
            "prioridad", "prioridad",
            "fechaSolicitud", "getFechaCreacionSolicitud",
            "fechaModificacion", "updatedAt",
            "solicitudEstatus", "solicitudEstatus"
    );
    private static final java.util.Set<String> FACTURACION_FILTERS = java.util.Set.of(
            "folio", "proveedor", "folioExterno", "totalOrden", "prioridad", "fechaSolicitud", "fechaModificacion", "estatusFactura", "fechaFacturacion"
    );
    private static final java.util.Map<String, String> FACTURACION_SORTS = java.util.Map.of(
            "folio", "folio",
            "proveedor", "proveedor.nombreComercial",
            "folioExterno", "folioExterno",
            "totalOrden", "totalOrden",
            "prioridad", "prioridad",
            "fechaSolicitud", "createdAt",
            "fechaModificacion", "updatedAt",
            "estatusFactura", "facturacionEstado",
            "fechaFacturacion", "fechaFacturacion"
    );
    private static final java.util.Set<String> RECIBO_FILTERS = java.util.Set.of(
            "folio", "proveedor", "prioridad", "fechaSolicitud", "fechaRecibo", "recepcionEstatus"
    );
    private static final java.util.Map<String, String> RECIBO_SORTS = java.util.Map.of(
            "folio", "folio",
            "proveedor", "proveedor.nombreComercial",
            "prioridad", "prioridad",
            "fechaSolicitud", "getFechaCreacionSolicitud",
            "fechaRecibo", "fechaRecibo",
            "recepcionEstatus", "recepcionEstatus"
    );

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public PageResponse<SolicitudOrdenResponseDTO> getPageSolicitud(Map<String, String> params) {
        CatalogPage query = CatalogPage.parse(params, SOLICITUD_FILTERS, SOLICITUD_SORTS.keySet());
        var result = ordenRepository.findAll(
                new OrdenPageSpecification(OrdenPageSpecification.Stage.SOLICITUD, query.search(), query.filters()),
                query.pageable(query.sort(SOLICITUD_SORTS, Sort.by(Sort.Direction.ASC, "folio"), "id"))
        );
        var items = result.getContent().stream()
                .map(row -> OrdenValueMapper.OrdenEntityToResponseDTO(row, row.getPartidas()))
                .toList();
        return new PageResponse<>(items, result.getTotalElements(), query.page(), query.pageSize());
    }

    public Specification<OrdenEntity> filterWithParameters(Map<String, String> params) {
        return new OrdenSpecification().getSpecificationByFilters(params);
    }

    public SolicitudOrdenResponseDTO cambiarEstado(UUID id, Map<String, SolicitudEstatus> acciones) {
        OrdenEntity query = this.ordenRepository.findById(id).orElseThrow(() -> new IDNotFound("No se encontró la id de la orden"));

        OrdenEntity finalQuery = query;
        acciones.forEach((key, value) -> {
            switch (key) {
                case "solicitudEstatus":
                    finalQuery.setSolicitudEstatus(value);
                    if (value == SolicitudEstatus.COMPLETADA) {
                        finalQuery.setFacturacionEstado(FacturacionOrdenEstatus.PENDIENTE);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Campo " + key + " no reconocido");
            }
        });

        query.setSolicitudEstatus(finalQuery.getSolicitudEstatus());
        query = ordenRepository.save(query);
        return OrdenValueMapper.OrdenEntityToResponseDTO(query, query.getPartidas());
    }

    //! LOGICA DE ORDEN DE COMPRA  //
    @Transactional
    public FacturacionOrdenResponseDTO updateFacturacion(FacturacionOrdenRequestDTO dto) {

        OrdenEntity query = findById(dto.getOrderId());

        // Si es null, o Cancelado, o Completado o Facturado no se puede editar
        if (query.getFacturacionEstado() != FacturacionOrdenEstatus.BORRADOR && query.getFacturacionEstado() != FacturacionOrdenEstatus.PENDIENTE) {
            throw new IDNotFound("El estado no es valido para editarlo");
        }

        query.setFacturacionEstado(dto.getFacturacionOrdenEstatus());
        query.setFolioExterno(dto.getFolioExterno());
        query.setComentarioFactura(dto.getComentarioFactura());

        if (dto.getFacturacionOrdenEstatus().equals(FacturacionOrdenEstatus.FACTURADO)) {
            query.setFechaFacturacion(LocalDateTime.now());
        }

        OrdenEntity dbOrden = this.ordenRepository.save(query);

        return OrdenValueMapper.OrdenFacturacionEntityToResponseDTO(dbOrden);
    }

    public FacturacionOrdenResponseDTO getByIdFactura(UUID id) {
        OrdenEntity query = findById(id);
        return OrdenValueMapper.OrdenFacturacionEntityToResponseDTO(query);
    }

    public List<FacturacionOrdenResponseDTO> getAllFacturacion() {
        Sort sort = Sort.by(Sort.Direction.ASC, "updatedAt"); // o el campo que necesites
//        List<OrdenEntity> list = ordenRepository.findAll(filterWithParameters(params), sort);
//        return list.stream().map((x) -> OrdenValueMapper.OrdenEntityToResponseDTO(x, x.getPartidas())).toList();

        List<OrdenEntity> list = ordenRepository.findByFacturacionEstadoNotNull(sort);
        return list.stream().map(OrdenValueMapper::OrdenFacturacionEntityToResponseDTO).toList();
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public PageResponse<FacturacionOrdenResponseDTO> getPageFacturacion(Map<String, String> params) {
        CatalogPage query = CatalogPage.parse(params, FACTURACION_FILTERS, FACTURACION_SORTS.keySet());
        var result = ordenRepository.findAll(
                new OrdenPageSpecification(OrdenPageSpecification.Stage.FACTURACION, query.search(), query.filters()),
                query.pageable(query.sort(FACTURACION_SORTS, Sort.by(Sort.Direction.ASC, "updatedAt"), "id"))
        );
        var items = result.getContent().stream().map(OrdenValueMapper::OrdenFacturacionEntityToResponseDTO).toList();
        return new PageResponse<>(items, result.getTotalElements(), query.page(), query.pageSize());
    }

    public RecepcionOrdenResponseDTO cambiarARecibo(UUID id, Map<String, FacturacionOrdenEstatus> accion) {

        OrdenEntity query = this.ordenRepository.findById(id).orElseThrow(() -> new IDNotFound("No se encontró la id de la orden"));

        query.setFolioExterno("N/A");
        query.setRecepcionEstatus(RecepcionEstatus.PENDIENTE);
        query = ordenRepository.save(query);
        return OrdenValueMapper.OrdenEntityToRecepcionOrdenResponseDTO(query);
    }

    //! LOGICA DE RECEPCION DE ORDEN DE COMPRA //

    public List<RecepcionOrdenResponseDTO> getAllRecibo() {
        List<OrdenEntity> list = ordenRepository.findByRecepcionEstatusNotNullOrderByUpdatedAtAsc();
        return list.stream().map(OrdenValueMapper::OrdenEntityToRecepcionOrdenResponseDTO).toList();
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public PageResponse<RecepcionOrdenResponseDTO> getPageRecibo(Map<String, String> params) {
        CatalogPage query = CatalogPage.parse(params, RECIBO_FILTERS, RECIBO_SORTS.keySet());
        var result = ordenRepository.findAll(
                new OrdenPageSpecification(OrdenPageSpecification.Stage.RECIBO, query.search(), query.filters()),
                query.pageable(query.sort(RECIBO_SORTS, Sort.by(Sort.Direction.ASC, "updatedAt"), "id"))
        );
        var items = result.getContent().stream().map(OrdenValueMapper::OrdenEntityToRecepcionOrdenResponseDTO).toList();
        return new PageResponse<>(items, result.getTotalElements(), query.page(), query.pageSize());
    }

    public RecepcionOrdenResponseDTO getByIdRecibo(UUID id) {
        OrdenEntity query = findById(id);
        return OrdenValueMapper.OrdenEntityToRecepcionOrdenResponseDTO(query);
    }

    @Transactional
    public RecepcionOrdenResponseDTO pasarARecibo(RecepcionOrdenRequestDTO dto) {
        OrdenEntity query = findById(dto.getIdOrden());

        List<PartidaOrdenEntity> partidas = dto.getPartidas().stream()
                .map((row) -> {

                    PartidaOrdenEntity editableRow = entityFinderService.findByPartida(row.getIdPartida());
                    if (row.getUnidadesRecibidas() == null) {
                        throw new IDNotFound("Las unidades recibidas son requeridas");
                    }
                    // Validamos que no reciban mas de las que pidieron.
                    if (row.getUnidadesRecibidas() > editableRow.getCantidad())
                        throw new IDNotFound("No se puede recibir mas unidades de las solicitadas" + "Unidades solicitadas: "
                                + editableRow.getCantidad());

                    PresentacionProductoBasico queryPresentacion = precioRepository.findById(editableRow.getPrecioRelacion().getId()).orElseThrow(
                            () -> new IDNotFound("Hubo un problema relaccionado con la busqueda de la presentacion del producto" + editableRow.getPrecioRelacion().getId())
                    );

                    int recibidasAnteriores = Optional.ofNullable(editableRow.getUnidadesRecibidas()).orElse(0);
                    int unidadesNuevas = row.getUnidadesRecibidas();
                    BigDecimal delta = BigDecimal.valueOf(unidadesNuevas - recibidasAnteriores);

                    editableRow.setUnidadesRecibidas(unidadesNuevas);
                    editableRow.setVerificado(row.isVerificado());
//                    editableRow.setComentario(row.getComentario());
                    if (Objects.nonNull(row.getIdAlmacen()) && delta.compareTo(BigDecimal.ZERO) != 0) {
                        AlmacenEntity queryAlmacen = this.almacenRepository.findById(row.getIdAlmacen()).orElseThrow(
                                () -> new IDNotFound("No se encontro la ID del almacen"));
                        log.info("Almacen encontrado: "+queryAlmacen.getNombre());
                        editableRow.setAlmacen(queryAlmacen);
                        Optional<InventarioEntity> queryInventario = inventarioRepository.findByAlmacen_IdAndProductoBasicoEntity_Id(
                                queryAlmacen.getId(),
                                queryPresentacion.getProductoBasicoEntity().getId());

                        if (queryInventario.isPresent()) {
                            log.info("Se encontró un inventario existente");
                            InventarioEntity inventario = queryInventario.get();
                            BigDecimal cantidadAnterior = inventario.getCantidad() == null ? BigDecimal.ZERO : inventario.getCantidad();
                            BigDecimal cantidadNueva = cantidadAnterior.add(delta);
                            if (cantidadNueva.compareTo(BigDecimal.ZERO) < 0) {
                                throw new IDNotFound("No hay suficiente inventario para ajustar la recepción");
                            }
                            inventario.setCantidad(cantidadNueva);

                            log.info("Cambio la cantidad de {} => {}", cantidadAnterior, inventario.getCantidad());
                            this.inventarioRepository.save(inventario);
                            if (delta.compareTo(BigDecimal.ZERO) < 0) {
                                stockMinimoRevisionService.revisarTrasSalida(queryPresentacion.getProductoBasicoEntity().getId());
                            }
                        } else if (delta.compareTo(BigDecimal.ZERO) > 0) {
                            log.info("Se ha creado un nuevo inventario.");
                            crearNuevoInventario(queryPresentacion, queryAlmacen, delta);
                        } else {
                            throw new IDNotFound("No se encontró inventario para revertir unidades recibidas");
                        }
                    } else if (Objects.nonNull(row.getIdAlmacen())) {
                        AlmacenEntity queryAlmacen = this.almacenRepository.findById(row.getIdAlmacen()).orElseThrow(
                                () -> new IDNotFound("No se encontro la ID del almacen"));
                        editableRow.setAlmacen(queryAlmacen);
                    }
                    return editableRow;
                }).toList(); // Recolectar las partidas en una lista
        partidaOrdenRepository.saveAll(partidas);
        query.setComentarioRecibo(dto.getComentarioRecibo());
        query.setFechaRecibo(LocalDateTime.now());
        query.setRecepcionEstatus(dto.getRecepcionEstatus());

        return OrdenValueMapper.OrdenEntityToRecepcionOrdenResponseDTO(ordenRepository.save(query));
    }

    private InventarioEntity crearNuevoInventario(PresentacionProductoBasico queryPresentacion, AlmacenEntity almacen, BigDecimal cantidad) {

        ProductoBasicoEntity producto = queryPresentacion.getProductoBasicoEntity();
        InventarioEntity newInventario = InventarioEntity.builder()
                .almacen(almacen)
                .productoBasicoEntity(producto)
                .cantidad(cantidad)
                .unidadDeMedida(producto == null ? null : producto.getUnidadMedidaDefault())
                .build();
        return this.inventarioRepository.save(newInventario);
    }

    //! FUNCIONES COMPLEMENTARIAS

    public List<Prioridad> getPriodidades() {
        return Arrays.stream(Prioridad.values()).toList();
    }

    private String generarFolio() {
        try {
            return jdbcTemplate.queryForObject("SELECT generar_idOrden()", String.class);
        } catch (Exception e) {
            throw new IDNotFound("Hubo un error al generar el folio" + e.getMessage());
        }

    }

    private OrdenEntity findById(UUID id) {
        return ordenRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new IDNotFound("Error: ID no encontrado"));
    }



    private PresentacionProductoBasico buscarOCrearRelaccion(PartidaOrdenRequestDTO row, ProveedorEntity proveedorRow, ProductoBasicoEntity producto) {

        boolean existe = precioRepository.existsByProductoBasicoEntity_IdAndProveedorEntity_Id(producto.getId(), proveedorRow.getId());

        PresentacionProductoBasico found = null;
        if(existe) {
            found = precioRepository.findByProductoBasicoEntity_IdAndProveedorEntity_Id(

                    producto.getId(),proveedorRow.getId()).orElseThrow(() -> new IDNotFound("No encontrado"));
        } else {
            log.info("No se encontro una instancia creando...");
            found = precioRepository.save(PresentacionProductoBasico.builder().proveedorEntity(proveedorRow)
                    .productoBasicoEntity(producto)
                    .precio(row.getPrecioUnitario())
                    .build());
        }
        return found;
    }
}

