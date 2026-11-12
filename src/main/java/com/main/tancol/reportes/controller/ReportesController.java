package com.main.tancol.reportes.controller;

import com.main.tancol.liquidacion.repositories.LiquidacionRepository;
import com.main.tancol.reportes.dto.request.*;
import com.main.tancol.reportes.dto.response.*;
import com.main.tancol.reportes.service.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/reportes")
@Tag(name = "Reportes")
@RequiredArgsConstructor
public class ReportesController {

    private final LiquidacionReporteService liquidacionReporteService;
    private final LiquidacionRepository liquidacionRepository;
    private final rpLiquidacionService rpLiquidacionService;
    private final ReporteGeneralCorridasService reporteGeneralCorridasService;
    private final BitacoraVentaReporteService bitacoraVentaReporteService;
    private final ConsumoAreaProductoReporteService consumoAreaProductoReporteService;
    private final ReporteVentasEmpleadosMetasService reporteVentasEmpleadosMetasService;
    private final SalidaMaterialReporteService salidaMaterialReporteService;

//  ! Liquidaciones   //
    @PostMapping("/liquidaciones")
    public ResponseEntity<List<ReporteLiquidacionesResponseDTO>> filtrarVentas(@RequestBody ReporteLiquidacionesRequestDTO filtro) {
        List<ReporteLiquidacionesResponseDTO> ventas = liquidacionReporteService.getLiquidaciones(
                filtro.getIdClientes(),
                filtro.getIdRutas(),
                filtro.getIdProductosComerciales(),
                filtro.getFechaInicio(),
                filtro.getFechaFin(),
                filtro.getTipoPago(),
                filtro.getConceptosVenta(),
                filtro.getFacturado(),
                filtro.getEstadoLiquidacion(),
                filtro.getX()
        );
        return ResponseEntity.ok(ventas);
    }

    @PostMapping("/liquidaciones-cuentas")
    public ResponseEntity<List<ReporteLiquidacionesCuentaResponseDTO>> ventasConPeso(@RequestBody ReporteLiquidacionesRequestDTO filtro) {
        List<ReporteLiquidacionesCuentaResponseDTO> ventas = liquidacionReporteService.getLiquidacionConCuentas(
                filtro.getIdClientes(),
                filtro.getIdRutas(),
                filtro.getIdProductosComerciales(),
                filtro.getFechaInicio(),
                filtro.getFechaFin(),
                filtro.getTipoPago(),
                filtro.getConceptosVenta(),
                filtro.getEstadoLiquidacion()
        );
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/liquidacion/download/{id}")
    public ResponseEntity<byte[]> descargarReporteLiquidacion(@PathVariable UUID id) {
        try {
            // Verificar que el producto existe
            if (!liquidacionRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            log.info("Generando reporte producto");
            // Generar el reporte PDF
            byte[] pdfBytes = rpLiquidacionService.generarReporte(id);

            // Configurar headers para PDF
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "producto_" + id + ".pdf");
            headers.setContentLength(pdfBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/liquidacion/view/{id}")
    public ResponseEntity<byte[]> verReporteLiquidacion(@PathVariable UUID id) {
        try {
            // Verificar que el producto existe
            if (!liquidacionRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            // Generar el reporte PDF
            byte[] pdfBytes = rpLiquidacionService.generarReporte(id);

            // Configurar headers para PDF
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "producto_" + id + ".pdf");
            headers.setContentLength(pdfBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

//  ! Liquidaciones   //


//?   V- Empleado
    @PostMapping("/volumen-ventas-empleado")
    public ResponseEntity<List<ReporteVolumenVentasResponseDTO>> filtrarVolumenEmpleado(@RequestBody ReporteVolumenVentasRequestDTO filtro){
        List<ReporteVolumenVentasResponseDTO> ventas = liquidacionReporteService.getVolumenPorEmpleado(
                filtro.getIdEmpledos(),
                filtro.getIdProductos(),
                filtro.getIdRutas(),
                filtro.getFechaInicio(),
                filtro.getFechaFin()
        );
        return ResponseEntity.ok(ventas);
    }

    //? V-CLIENTE
    @PostMapping("/volumen-ventas-cliente")
    public ResponseEntity<List<ReporteTotalizadoClienteResponseDTO>> filtrarVolumenCliente(@RequestBody ReporteTotalizadoClienteRequestDTO filtro){
        List<ReporteTotalizadoClienteResponseDTO> ventas = liquidacionReporteService.getVolumenPorCliente(
                filtro.getIdCliente(),
                filtro.getIdProductosComerciales(),
                filtro.getFechaInicio(),
                filtro.getFechaFin()
        );
        return ResponseEntity.ok(ventas);
    }

//? V-EMPLEADO-DIA
    @PostMapping("/volumen-ventas-empleado-dia")
    public ResponseEntity<List<ReporteVVEDiaResponseDTO>> filtrarVolumenEmpleadoDia(@RequestBody ReporteVolumenVentasRequestDTO filtro){
    List<ReporteVVEDiaResponseDTO> ventas = liquidacionReporteService.getVolumenPorEmpleadoDia(
            filtro.getIdEmpledos(),
            filtro.getIdProductos(),
            filtro.getIdRutas(),
            filtro.getConceptosVenta(),
            filtro.getFacturado(),
            filtro.getFechaInicio(),
            filtro.getFechaFin()
    );
    return ResponseEntity.ok(ventas);
}

    // Reportes general corridas (scripts SQL)
    @PostMapping("/general/rg-factura")
    public ResponseEntity<ReporteGeneralCorridasResponseDTO> reporteRgFactura(
            @RequestBody ReporteGeneralRangoRequestDTO filtro) {
        return ResponseEntity.ok(reporteGeneralCorridasService.ejecutarRgFactura(
                filtro.getFechaInicio(), filtro.getFechaFin()));
    }

    @PostMapping("/general/rg-nofactura")
    public ResponseEntity<ReporteGeneralCorridasResponseDTO> reporteRgNofactura(
            @RequestBody ReporteGeneralRangoRequestDTO filtro) {
        return ResponseEntity.ok(reporteGeneralCorridasService.ejecutarRgNofactura(
                filtro.getFechaInicio(), filtro.getFechaFin()));
    }

    @PostMapping("/general/rg-nofactura-autoservicio")
    public ResponseEntity<ReporteGeneralCorridasResponseDTO> reporteRgNofacturaAutoservicio(
            @RequestBody ReporteGeneralRangoRequestDTO filtro) {
        return ResponseEntity.ok(reporteGeneralCorridasService.ejecutarRgNofacturaAutoservicio(
                filtro.getFechaInicio(), filtro.getFechaFin()));
    }

    @PostMapping("/general/rg-credito")
    public ResponseEntity<ReporteGeneralCorridasResponseDTO> reporteRgCredito(
            @RequestBody ReporteGeneralRangoRequestDTO filtro) {
        return ResponseEntity.ok(reporteGeneralCorridasService.ejecutarRgCredito(
                filtro.getFechaInicio(), filtro.getFechaFin()));
    }

    @PostMapping("/general/rp-tdebito")
    public ResponseEntity<ReporteGeneralCorridasResponseDTO> reporteRpTdebito(
            @RequestBody ReporteGeneralRangoRequestDTO filtro) {
        return ResponseEntity.ok(reporteGeneralCorridasService.ejecutarRpTdebito(
                filtro.getFechaInicio(), filtro.getFechaFin()));
    }

    @PostMapping("/general/rp-tcredito")
    public ResponseEntity<ReporteGeneralCorridasResponseDTO> reporteRpTcredito(
            @RequestBody ReporteGeneralRangoRequestDTO filtro) {
        return ResponseEntity.ok(reporteGeneralCorridasService.ejecutarRpTcredito(
                filtro.getFechaInicio(), filtro.getFechaFin()));
    }

    @PostMapping("/bitacoras-ventas")
    public ResponseEntity<List<ReporteBitacorasVentasResponseDTO>> filtrarBitacorasVentas(
            @RequestBody ReporteBitacorasVentasRequestDTO filtro) {
        return ResponseEntity.ok(bitacoraVentaReporteService.getBitacorasVentas(filtro));
    }

    @PostMapping("/consumo-area-producto")
    public ResponseEntity<ReporteConsumoAreaProductoResponseDTO> reporteConsumoAreaProducto(
            @RequestBody ReporteConsumoAreaProductoRequestDTO filtro) {
        return ResponseEntity.ok(consumoAreaProductoReporteService.generar(filtro));
    }

    @PostMapping("/ventas-empleados-metas")
    public ResponseEntity<List<ReporteVentasEmpleadosMetasResponseDTO>> ventasEmpleadosMetas(
            @RequestBody ReporteVentasEmpleadosMetasRequestDTO filtro) {
        return ResponseEntity.ok(reporteVentasEmpleadosMetasService.generar(filtro));
    }

    @PostMapping("/salida-material")
    public ResponseEntity<ReporteSalidaMaterialResponseDTO> reporteSalidaMaterial(
            @RequestBody ReporteSalidaMaterialRequestDTO filtro) {
        return ResponseEntity.ok(salidaMaterialReporteService.generar(filtro));
    }

}
