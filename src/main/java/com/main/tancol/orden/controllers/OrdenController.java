package com.main.tancol.orden.controllers;

import com.main.tancol.orden.dto.request.OrdenFacturacionRequestDTO;
import com.main.tancol.orden.dto.request.RecepcionOrdenRequestDTO;
import com.main.tancol.orden.dto.request.SolicitudOrdenRequestDTO;
import com.main.tancol.orden.dto.response.OrdenFacturacionResponseDTO;
import com.main.tancol.orden.dto.response.RecepcionOrdenResponseDTO;
import com.main.tancol.orden.dto.response.SolicitudOrdenResponseDTO;
import com.main.tancol.orden.enums.FacturacionOrdenEstatus;
import com.main.tancol.orden.enums.Prioridad;
import com.main.tancol.orden.enums.RecepcionEstatus;
import com.main.tancol.orden.enums.SolicitanteEstatus;
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
    public ResponseEntity<SolicitudOrdenResponseDTO> create(@Parameter(example = esquema_creacion) @Valid @RequestBody SolicitudOrdenRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto));
    }

    @GetMapping("/prioridades")
    public ResponseEntity<List<Prioridad>> getPriodidades(){
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
        return ResponseEntity.ok(service.update(id,dto));
    }

    @PatchMapping("/solicitud/{id}&{accion}")
    public ResponseEntity<SolicitudOrdenResponseDTO> cambiarEstado(@PathVariable UUID id, @PathVariable SolicitanteEstatus accion) {
        return ResponseEntity.ok(service.cambiarEstado(id,accion));
    }

    @GetMapping("/facturacion")
    public ResponseEntity<List<OrdenFacturacionResponseDTO>> getAllFacturacion() {
        return ResponseEntity.ok(service.getAllFacturacion());
    }

    @GetMapping("/facturacion/{id}")
    public ResponseEntity<OrdenFacturacionResponseDTO> getByIdFacturacion(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getByIdFactura(id));
    }

    @PostMapping("/facturacion")
    public ResponseEntity<OrdenFacturacionResponseDTO> updateFacturacion(@RequestBody OrdenFacturacionRequestDTO dto) {
        return ResponseEntity.ok(service.updateFacturacion(dto));
    }

    @PatchMapping("/facturacion/{id}&{accion}")
    public ResponseEntity<RecepcionOrdenResponseDTO> cambiarEstado(@PathVariable UUID id, @PathVariable RecepcionEstatus accion) {
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
    public ResponseEntity<RecepcionOrdenResponseDTO> postRecepcion(@RequestBody RecepcionOrdenRequestDTO dto) {
        return ResponseEntity.ok(service.updateRecibo(dto));
    }

//    @PutMapping("/recibo/{id}")
//    public ResponseEntity<RecepcionOrdenResponseDTO> updateRecepcion(@RequestBody RecepcionOrdenRequestDTO dto) {
//        return ResponseEntity.ok(service.updateRecibo(dto));
//    }

//    @PutMapping("/facturar/{id}")
//    public ResponseEntity<OrdenResponseDTO> facturar(@PathVariable UUID id, @RequestBody OrdenFacturacionRequestDTO dto) {
//        return ResponseEntity.ok(service.facturar(id,dto));
//    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<SolicitudOrdenResponseDTO> delete(@Valid @PathVariable UUID id) {
//        return ResponseEntity.ok(service.delete(id));
//    }

    private final String esquema = """
{
"id": "f809b1bf-f973-4aae-9d4f-3413ca3c1b76",
"titulo": "Orden 1",
"folio": "SC-25-01",
"prioridad": "ALTA"
}\
""";

    private final String esquema_creacion = """
{
  "proveedorId": "68c0ca9c-8d23-4499-9756-4846f03f69c6",
  "fechaSolicitud": "2025-03-08T19:05:47.488Z",
  "prioridad": "ALTA",
  "titulo": "string",
  "solicitanteEstatus": "PENDIENTE",
  "partidas": [
    {
      "idProducto": "be1a7022-47b4-4ad5-a1cb-4a3772d52eac",
      "idTipoImpuesto": "1e023295-22c4-4376-b682-6ed15ec38543",
      "cantidad": 0,
      "precio": 0
    }
  ]
}\
""";

}
