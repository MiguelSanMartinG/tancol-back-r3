package com.main.tancol.reportes.service;

import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.liquidacion.entities.LiquidacionEntity;
import com.main.tancol.liquidacion.entities.LiquidacionPartidaEntity;
import com.main.tancol.liquidacion.repositories.LiquidacionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class rpLiquidacionService {
    private final LiquidacionRepository liquidacionRepository;

    public byte[] generarReporte(UUID liquidacionId) throws JRException {
        LiquidacionEntity liquidacion = liquidacionRepository.findById(liquidacionId)
                .orElseThrow(() -> new IDNotFound("Liquidación no encontrada con ID: " + liquidacionId));

        // Cargar el reporte principal
        InputStream jasperStream = this.getClass().getResourceAsStream("/reports/producto_report.jrxml");
        if (jasperStream == null) {
            throw new IDNotFound("Error al cargar el archivo de reporte principal");
        }

        // Compilar el subreporte también (necesario para la tabla)
        InputStream partidasSubreportStream = this.getClass().getResourceAsStream("/reports/partidas_subreport.jrxml");
        if (partidasSubreportStream == null) {
            throw new IDNotFound("Error al cargar el subreporte de partidas");
        }

        JasperReport partidasSubreport = JasperCompileManager.compileReport(partidasSubreportStream);

        log.info("Generando reporte para liquidación: {}", liquidacion.getFolio());

        JasperReport jasperReport = JasperCompileManager.compileReport(jasperStream);

//         Crear datos de ejemplo para las partidas (reemplaza con tus datos reales)
        List<PartidaData> partidas = obtenerPartidasDeLiquidacion(liquidacion);
        JRBeanCollectionDataSource partidasDataSource = new JRBeanCollectionDataSource(partidas);

        // Preparar los parámetros
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("REPORTE_ID", liquidacion.getFolio().toString());
        parameters.put("BLOQUE_NOTA", liquidacion.getBloqueNota().toString());
        parameters.put("FECHA", liquidacion.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        // Si tienes estos campos en LiquidacionEntity, descoméntalos y ajústalos
        parameters.put("PRODUCTO_PRECIO", new BigDecimal("0.00")); // Reemplazar con dato real
        parameters.put("PRODUCTO_STOCK", 0); // Reemplazar con dato real
        parameters.put("PRODUCTO_CATEGORIA", "Sin categoría"); // Reemplazar con dato real
        parameters.put("FECHA_CREACION", liquidacion.getCreatedAt().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        parameters.put("FECHA_REPORTE", LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

        // IMPORTANTE: Agregar el datasource de partidas
        parameters.put("PARTIDAS_DATASOURCE", partidasDataSource);

        // Agregar el subreporte compilado como parámetro (si es necesario)
        parameters.put("PARTIDAS_SUBREPORT", partidasSubreport);

        JREmptyDataSource dataSource = new JREmptyDataSource();

        JasperPrint jasperPrint = JasperFillManager.fillReport(
                jasperReport, parameters, dataSource);

        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    /**
     * Metodo para obtener las partidas de una liquidación
     * Reemplaza este metodo con tu lógica real de obtención de datos
     */
    private List<PartidaData> obtenerPartidasDeLiquidacion(LiquidacionEntity liquidacion) {

//        if(liquidacion.getLiquidacionPartida().isEmpty()) throw new IDNotFound("Hubo un problema");

        List<LiquidacionPartidaEntity> partidasLiquidacion = liquidacion.getLiquidacionPartida();

        List<PartidaData> partidas = new ArrayList<>();
        log.info(partidasLiquidacion.size() + "");

//        partidas.add(new PartidaData(
//                "4",
//                "Cliente 2",
//                new BigDecimal("200.00"),
//                new BigDecimal("200.00"),
//                new BigDecimal("1000.00"),
//                "Nombre Alt B",
//                "Tipo 2",
//                "Sin comentarios"
//        ));

        try {
            for(LiquidacionPartidaEntity partida : partidasLiquidacion) {

                log.info(partida.getIdPartida().toString());
//
                partidas.add(new PartidaData(
                        ObjectUtils.isNotEmpty(partida.getProductoComercial()) ? partida.getProductoComercial().getSku() : "N/A",
                        ObjectUtils.isNotEmpty(partida.getCliente()) ? partida.getCliente().getNombreComercial() : "N/A",
                        ObjectUtils.isNotEmpty(partida.getCantidad()) ? partida.getPrecio() : new BigDecimal("-1"),
                        ObjectUtils.isNotEmpty(partida.getPrecio()) ? partida.getImporte() : new BigDecimal("-1"),
                        ObjectUtils.isNotEmpty(partida.getImporte()) ? partida.getCantidad() : new BigDecimal("-1"),
                        ObjectUtils.isNotEmpty(partida.getProductoComercial()) ? partida.getProductoComercial().getNombre() : "N/A",
                        ObjectUtils.isNotEmpty(partida.getFormaPago()) ? partida.getFormaPago().toString() : "N/A",
                        "Sin comentarios"
                ));

            }
        }catch (Exception e) {
            log.error(e.getMessage());
        }


        return partidas;
    }

}
        // EJEMPLO: Crear partidas de ejemplo - reemplaza con tu lógica real
        // Si tienes una relación con una entidad de partidas, úsala aquí

//        // Ejemplo de datos ficticios - ELIMINAR cuando tengas datos reales

//


        // Si tienes una relación JPA como liquidacion.getPartidas(), úsala así:
        /*
        if (liquidacion.getPartidas() != null) {
            for (PartidaEntity partida : liquidacion.getPartidas()) {
                partidas.add(new PartidaData(
                    partida.getNombre(),
                    partida.getCliente(),
                    partida.getCantidad(),
                    partida.getPrecio(),
                    partida.getImporte(),
                    partida.getNombre2(),
                    partida.getTipos(),
                    partida.getComentario()
                ));
            }
        }
        */


