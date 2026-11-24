package com.main.tancol.inventariodiario.controller;

import com.main.tancol.inventariodiario.dto.InvDiarioDTOs.*;
import com.main.tancol.inventariodiario.enums.EstatusSolicitud;
import com.main.tancol.inventariodiario.service.InventarioDiarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Inventario diario de insumos y solicitudes de stock (contrato del Buzón 1 y 2). */
@RestController
@RequestMapping("/inventario-diario")
@RequiredArgsConstructor
@Tag(name = "📦 Inventario diario")
public class InventarioDiarioController {

    private final InventarioDiarioService service;

    // ------------------------------------------------------------------ catálogos y permisos
    @GetMapping("/catalogos")
    public Catalogos catalogos() {
        return service.catalogos();
    }

    @GetMapping("/almacenes/{almacenId}/insumos")
    public List<Insumo> insumosDeAlmacen(@PathVariable UUID almacenId) {
        return service.insumosDeAlmacen(almacenId);
    }

    @GetMapping("/mis-permisos")
    public MisPermisos misPermisos() {
        return service.misPermisos();
    }

    // ------------------------------------------------------------------ cupos
    @GetMapping("/areas/{areaId}/cupos")
    public List<Cupo> cupos(@PathVariable UUID areaId,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return service.cupos(areaId, fecha);
    }

    @PostMapping("/cupos")
    public Cupo crearCupo(@RequestBody CupoCrear dto) {
        return service.crearCupo(dto);
    }

    @PutMapping("/cupos/{id}")
    public Cupo editarCupo(@PathVariable UUID id, @RequestBody CupoEditar dto) {
        return service.editarCupo(id, dto);
    }

    @DeleteMapping("/cupos/{id}")
    public ResponseEntity<Void> eliminarCupo(@PathVariable UUID id) {
        service.eliminarCupo(id);
        return ResponseEntity.noContent().build();
    }

    // ------------------------------------------------------------------ confirmación del día
    /** null (200 sin cuerpo) si el día no se ha confirmado. */
    @GetMapping("/areas/{areaId}/confirmacion")
    public Confirmacion confirmacion(@PathVariable UUID areaId,
                                     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return service.confirmacion(areaId, fecha);
    }

    @GetMapping("/areas/{areaId}/confirmacion/previa")
    public List<PreviaCupo> previa(@PathVariable UUID areaId) {
        return service.previa(areaId);
    }

    @PostMapping("/confirmaciones")
    public ResultadoConfirmacion confirmar(@RequestBody ConfirmarDia dto) {
        return service.confirmar(dto);
    }

    // ------------------------------------------------------------------ partidas
    @GetMapping("/partidas")
    public List<Partida> partidas(@RequestParam(required = false) UUID areaId,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
                                  @RequestParam(required = false) UUID empleadoId) {
        return service.partidas(areaId, inicio, fin, empleadoId);
    }

    @PostMapping("/partidas")
    public Partida crearPartida(@RequestBody PartidaCrear dto) {
        return service.crearPartida(dto);
    }

    @PutMapping("/partidas/{id}")
    public Partida editarPartida(@PathVariable UUID id, @RequestBody PartidaEditar dto) {
        return service.editarPartida(id, dto);
    }

    /** DELETE con cuerpo { ids: [] }; solo borra las que no tienen firma. */
    @DeleteMapping("/partidas")
    public Eliminadas eliminarPartidas(@RequestBody Ids dto) {
        return service.eliminarPartidas(dto);
    }

    /** Firma: multipart con «archivo» (imagen) o «idImagenFirma» de una imagen ya subida. */
    @PostMapping(value = "/partidas/{id}/firma", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Partida firmarPartida(@PathVariable UUID id,
                                 @RequestPart(value = "archivo", required = false) MultipartFile archivo,
                                 @RequestParam(value = "idImagenFirma", required = false) String idImagenFirma) {
        return service.firmarPartida(id, archivo, idImagenFirma);
    }

    // ------------------------------------------------------------------ solicitudes de stock
    @GetMapping("/solicitudes")
    public List<Solicitud> solicitudes(@RequestParam(required = false) UUID areaId,
                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
                                       @RequestParam(required = false) UUID empleadoId,
                                       @RequestParam(required = false) EstatusSolicitud estatus,
                                       @RequestParam(required = false) String folio) {
        return service.solicitudes(areaId, inicio, fin, empleadoId, estatus, folio);
    }

    @PostMapping("/solicitudes")
    public Solicitud solicitar(@RequestBody SolicitudCrear dto) {
        return service.solicitar(dto);
    }

    @PutMapping("/solicitudes/{id}")
    public Solicitud editarSolicitud(@PathVariable UUID id, @RequestBody SolicitudEditar dto) {
        return service.editarSolicitud(id, dto);
    }

    @PutMapping("/solicitudes/{id}/cancelar")
    public Solicitud cancelar(@PathVariable UUID id) {
        return service.cancelar(id);
    }

    @PutMapping("/solicitudes/{id}/aceptar")
    public Solicitud aceptar(@PathVariable UUID id, @RequestBody Aceptar dto) {
        return service.aceptar(id, dto);
    }

    @PutMapping("/solicitudes/{id}/rechazar")
    public Solicitud rechazar(@PathVariable UUID id, @RequestBody Rechazar dto) {
        return service.rechazar(id, dto);
    }

    @PutMapping(value = "/solicitudes/{id}/recibir", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Solicitud recibir(@PathVariable UUID id,
                             @RequestParam(value = "cantidadRecibida", required = false) BigDecimal cantidadRecibida,
                             @RequestPart(value = "archivo", required = false) MultipartFile archivo,
                             @RequestParam(value = "idImagenFirma", required = false) String idImagenFirma) {
        return service.recibir(id, cantidadRecibida, archivo, idImagenFirma);
    }
}
