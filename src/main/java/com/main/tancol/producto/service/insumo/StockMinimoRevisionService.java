package com.main.tancol.producto.service.insumo;

import com.main.tancol.almacen.repositories.InventarioRepository;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.notificacion.service.NotificacionService;
import com.main.tancol.producto.dto.response.insumos.StockMinimoItemResponseDTO;
import com.main.tancol.producto.dto.response.insumos.StockMinimoRevisionResponseDTO;
import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import com.main.tancol.producto.repositories.ProductoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class StockMinimoRevisionService {

    public static final ZoneId ZONA_MEXICO = ZoneId.of("America/Mexico_City");

    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;
    private final NotificacionService notificacionService;

    public StockMinimoRevisionService(ProductoRepository productoRepository,
                                      InventarioRepository inventarioRepository,
                                      NotificacionService notificacionService) {
        this.productoRepository = productoRepository;
        this.inventarioRepository = inventarioRepository;
        this.notificacionService = notificacionService;
    }

    /**
     * Revisión manual del stock total contra el mínimo del insumo.
     * Genera notificaciones a usuarios con lectura en el módulo de insumos, aunque ya existan iguales.
     */
    @Transactional
    public StockMinimoRevisionResponseDTO revisarStockMinimo() {
        LocalDateTime ejecutadoEn = ZonedDateTime.now(ZONA_MEXICO).toLocalDateTime();
        log.info("Iniciando revisión de stock mínimo de insumos a {}", ejecutadoEn);

        List<ProductoBasicoEntity> productos =
                productoRepository.findByDeletedFalseAndEnabledTrueAndNotificarStockMinimoTrueAndStockMinimoIsNotNull();
        Map<UUID, BigDecimal> stockPorProducto = sumarStock(productos);

        List<StockMinimoItemResponseDTO> bajoMinimo = new ArrayList<>();
        for (ProductoBasicoEntity producto : productos) {
            BigDecimal stockActual = stockPorProducto.getOrDefault(producto.getId(), BigDecimal.ZERO);
            if (stockActual.compareTo(producto.getStockMinimo()) < 0) {
                bajoMinimo.add(StockMinimoItemResponseDTO.builder()
                        .idProducto(producto.getId())
                        .nombre(producto.getNombre())
                        .sku(producto.getSku())
                        .stockMinimo(producto.getStockMinimo())
                        .stockActual(stockActual)
                        .build());
            }
        }

        int notificacionesGeneradas = 0;
        int destinatarios = 0;
        if (!bajoMinimo.isEmpty()) {
            List<EmpleadosEntity> destinatariosList = notificacionService.destinatariosInsumos();
            destinatarios = destinatariosList.size();
            if (destinatariosList.isEmpty()) {
                log.warn("Hay {} insumos bajo el stock mínimo pero no hay destinatarios con lectura en módulo '{}'",
                        bajoMinimo.size(), NotificacionService.MODULO_INSUMOS);
            } else {
                log.info("Destinatarios de notificación de stock mínimo: {}", destinatarios);
                for (StockMinimoItemResponseDTO item : bajoMinimo) {
                    notificacionesGeneradas += notificarBajoMinimo(item, destinatariosList);
                }
            }
        }

        log.info("Revisión de stock mínimo finalizada. Evaluadas={}, bajo mínimo={}, destinatarios={}, notificaciones={}",
                productos.size(), bajoMinimo.size(), destinatarios, notificacionesGeneradas);

        return StockMinimoRevisionResponseDTO.builder()
                .ejecutadoEn(ejecutadoEn)
                .totalEvaluadas(productos.size())
                .totalBajoMinimo(bajoMinimo.size())
                .destinatariosEncontrados(destinatarios)
                .notificacionesGeneradas(notificacionesGeneradas)
                .bajoMinimo(bajoMinimo)
                .build();
    }

    private Map<UUID, BigDecimal> sumarStock(List<ProductoBasicoEntity> productos) {
        if (productos.isEmpty()) {
            return Map.of();
        }
        List<UUID> ids = productos.stream().map(ProductoBasicoEntity::getId).toList();
        Map<UUID, BigDecimal> stockPorProducto = new HashMap<>();
        for (Object[] fila : inventarioRepository.sumCantidadByProductoIds(ids)) {
            BigDecimal cantidad = (BigDecimal) fila[1];
            stockPorProducto.put((UUID) fila[0], cantidad != null ? cantidad : BigDecimal.ZERO);
        }
        return stockPorProducto;
    }

    private int notificarBajoMinimo(StockMinimoItemResponseDTO item, List<EmpleadosEntity> destinatarios) {
        String sku = (item.getSku() == null || item.getSku().isBlank()) ? "N/D" : item.getSku();
        String titulo = "Stock bajo el mínimo";
        String mensaje = String.format(
                "El insumo %s (SKU: %s) está bajo el stock mínimo. Stock actual: %s, mínimo: %s.",
                item.getNombre(),
                sku,
                item.getStockActual().toPlainString(),
                item.getStockMinimo().toPlainString()
        );
        return notificacionService.crearStockMinimo(destinatarios, titulo, mensaje, item.getIdProducto());
    }

    /**
     * Tras una salida de almacén. Si el insumo tiene el checkbox activo y el stock total
     * quedó bajo el mínimo, avisa una vez mientras esa alerta siga sin leerse.
     */
    @Transactional
    public void revisarTrasSalida(UUID productoId) {
        if (productoId == null) {
            return;
        }
        ProductoBasicoEntity producto = productoRepository.findById(productoId).orElse(null);
        if (producto == null
                || producto.isDeleted()
                || !producto.isEnabled()
                || !producto.isNotificarStockMinimo()
                || producto.getStockMinimo() == null) {
            return;
        }
        if (notificacionService.hayAlertaStockMinimoPendiente(productoId)) {
            return;
        }

        BigDecimal stockActual = sumarStock(List.of(producto)).getOrDefault(productoId, BigDecimal.ZERO);
        if (stockActual.compareTo(producto.getStockMinimo()) >= 0) {
            return;
        }

        List<EmpleadosEntity> destinatarios = notificacionService.destinatariosInsumos();
        if (destinatarios.isEmpty()) {
            log.warn("El insumo {} quedó bajo el stock mínimo pero no hay destinatarios con lectura en módulo '{}'",
                    productoId, NotificacionService.MODULO_INSUMOS);
            return;
        }

        StockMinimoItemResponseDTO item = StockMinimoItemResponseDTO.builder()
                .idProducto(producto.getId())
                .nombre(producto.getNombre())
                .sku(producto.getSku())
                .stockMinimo(producto.getStockMinimo())
                .stockActual(stockActual)
                .build();
        int creadas = notificarBajoMinimo(item, destinatarios);
        log.info("Salida de almacén dejó el insumo {} bajo el mínimo. Stock={}, mínimo={}, notificaciones={}",
                productoId, stockActual, producto.getStockMinimo(), creadas);
    }

    /**
     * Tras devolver unidades al almacén. Si el stock total ya cubre el mínimo, apaga la alerta pendiente.
     */
    @Transactional
    public void revisarTrasDevolucion(UUID productoId) {
        if (productoId == null) {
            return;
        }
        ProductoBasicoEntity producto = productoRepository.findById(productoId).orElse(null);
        if (producto == null || producto.getStockMinimo() == null) {
            return;
        }
        BigDecimal stockActual = sumarStock(List.of(producto)).getOrDefault(productoId, BigDecimal.ZERO);
        if (stockActual.compareTo(producto.getStockMinimo()) >= 0) {
            notificacionService.desactivarStockMinimo(productoId);
        }
    }
}
