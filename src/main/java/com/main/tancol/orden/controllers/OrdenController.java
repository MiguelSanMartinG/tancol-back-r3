package com.main.tancol.orden.controllers;

import com.main.tancol.orden.dto.request.FacturacionOrdenRequestDTO;
import com.main.tancol.orden.dto.request.RecepcionOrdenRequestDTO;
import com.main.tancol.orden.dto.request.SolicitudOrdenRequestDTO;
import com.main.tancol.orden.dto.response.FacturacionOrdenResponseDTO;
import com.main.tancol.orden.dto.response.RecepcionOrdenResponseDTO;
import com.main.tancol.orden.dto.response.SolicitudOrdenResponseDTO;
import com.main.tancol.orden.enums.FacturacionOrdenEstatus;
import com.main.tancol.orden.enums.Prioridad;
import com.main.tancol.orden.enums.SolicitudEstatus;
import com.main.tancol.orden.service.OrdenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/orden")
@Tag(name = "\uD83D\uDCDC Orden")
public class OrdenController {

    private final OrdenService service;

    public OrdenController(OrdenService service) {
        this.service = service;
    }

    @PostMapping("/solicitud")
    @Operation(summary = "PASO 1: CREAR SOLICITUD")
    public ResponseEntity<SolicitudOrdenResponseDTO> create(@Valid @RequestBody SolicitudOrdenRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto));
    }

    @GetMapping("/prioridades")
    @Operation(summary = "PRIORIDADES")
    public ResponseEntity<List<Prioridad>> getPriodidades() {
        return ResponseEntity.status(HttpStatus.OK).body(this.service.getPriodidades());
    }

    @GetMapping("/solicitud")
    @Operation()
    public ResponseEntity<List<SolicitudOrdenResponseDTO>> findAll(@Parameter(
            name = "Filtros",
            description = "Un objeto JSON que contiene los filtros de búsqueda.",
            example = esquema
    ) @RequestParam Map<String, String> params) {
        List<SolicitudOrdenResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/solicitud/{id}")
    public ResponseEntity<SolicitudOrdenResponseDTO> getByIdSolicitud(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/solicitud/{id}")
    public ResponseEntity<SolicitudOrdenResponseDTO> accionSolicitudOrden(@PathVariable UUID id, @RequestBody SolicitudOrdenRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }


    @GetMapping("/facturacion")
    public ResponseEntity<List<FacturacionOrdenResponseDTO>> getAllFacturacion() {
        return ResponseEntity.ok(service.getAllFacturacion());
    }

    @GetMapping("/facturacion/{id}")
    public ResponseEntity<FacturacionOrdenResponseDTO> getByIdFacturacion(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getByIdFactura(id));
    }

    @PostMapping("/facturacion")
    @Operation(summary = "PASO 2: FACTURAR")
    public ResponseEntity<FacturacionOrdenResponseDTO> updateFacturacion(@RequestBody FacturacionOrdenRequestDTO dto) {
        return ResponseEntity.ok(service.updateFacturacion(dto));
    }


    @PatchMapping("/solicitud/{id}")
    @Operation(summary = "PASO 1.1: PASAR A FACTURAR")
    public ResponseEntity<SolicitudOrdenResponseDTO> cambiarEstadoSolicitud(
            @Parameter(
                    name = "Cambiar estado solicitud",
                    description = "    PENDIENTE,\n" +
                            "    COMPLETADA,\n" +
                            "    FACTURADO,\n" +
                            "    BORRADOR\n",
                    example = esquema_orden
            )
            @PathVariable UUID id, @RequestBody Map<String, SolicitudEstatus> accion) {
        return ResponseEntity.ok(service.cambiarEstado(id, accion));
    }

    @PatchMapping("/facturacion/{id}")
    @Operation(summary = "PASO 2.1: CAMBIAR A RECIBO")
    public ResponseEntity<RecepcionOrdenResponseDTO> cambiarEstadoFactura(@PathVariable UUID id, @Parameter( name = "Cambiar a Recibo",example = esquema_facturacion) @RequestBody Map<String, FacturacionOrdenEstatus> accion) {
        return ResponseEntity.ok(service.cambiarARecibo(id, accion));
    }

    @GetMapping("/recibo")
    public ResponseEntity<List<RecepcionOrdenResponseDTO>> getAllRecibo() {
        return ResponseEntity.ok(service.getAllRecibo());
    }

    @GetMapping("/recibo/{id}")
    public ResponseEntity<RecepcionOrdenResponseDTO> getByIdRecepcion(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getByIdRecibo(id));
    }

    @PostMapping("/recibo")
    @Operation(summary = "PASO 3: RECEPCION")
    public ResponseEntity<RecepcionOrdenResponseDTO> postRecepcion(@RequestBody RecepcionOrdenRequestDTO dto) {
        return ResponseEntity.ok(service.pasarARecibo(dto));
    }

    private final String esquema = """
            {
            }\
            """;

    private final String esquema_orden = """
            {
              "SolicitudEstatus": "ORDEN_COMPLETADA"
            }\
            """;

    private final String esquema_facturacion = """
            {
              "FacturacionOrdenEstatus": "ORDEN_COMPLETADA"
            }\
            """;
}
