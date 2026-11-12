package com.main.tancol.notificacion.controllers;

import com.main.tancol.notificacion.dto.request.AsignacionNotificacionRolRequestDTO;
import com.main.tancol.notificacion.dto.request.NotificacionDebugRequestDTO;
import com.main.tancol.notificacion.dto.request.NotificacionTipoUpdateRequestDTO;
import com.main.tancol.notificacion.dto.response.NotificacionResponseDTO;
import com.main.tancol.notificacion.dto.response.NotificacionTipoResponseDTO;
import com.main.tancol.notificacion.service.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notificaciones")
@Tag(name = "Notificaciones")
public class NotificacionController {

    private final NotificacionService service;

    public NotificacionController(NotificacionService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<NotificacionResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarDelEmpleadoActual());
    }

    @GetMapping("/tipos")
    public ResponseEntity<List<NotificacionTipoResponseDTO>> listarTipos() {
        return ResponseEntity.ok(service.listarTipos());
    }

    @PutMapping("/tipos/{codigo}")
    public ResponseEntity<NotificacionTipoResponseDTO> actualizarTipo(
            @PathVariable String codigo,
            @Valid @RequestBody NotificacionTipoUpdateRequestDTO dto) {
        return ResponseEntity.ok(service.actualizarTipo(codigo, dto));
    }

    @GetMapping("/roles/{roleId}")
    public ResponseEntity<List<NotificacionTipoResponseDTO>> listarTiposDelRol(@PathVariable Long roleId) {
        return ResponseEntity.ok(service.listarTiposDelRol(roleId));
    }

    @PutMapping("/roles/{roleId}")
    public ResponseEntity<List<NotificacionTipoResponseDTO>> reemplazarTiposDelRol(
            @PathVariable Long roleId,
            @Valid @RequestBody AsignacionNotificacionRolRequestDTO dto) {
        return ResponseEntity.ok(service.reemplazarTiposDelRol(roleId, dto));
    }

    @PutMapping("/{id}/leer")
    public ResponseEntity<NotificacionResponseDTO> marcarLeida(@PathVariable UUID id) {
        return ResponseEntity.ok(service.marcarLeida(id));
    }

    @PostMapping("/debug")
    @Operation(summary = "Genera una notificación de prueba para el usuario autenticado")
    public ResponseEntity<NotificacionResponseDTO> crearDebug(
            @RequestBody(required = false) NotificacionDebugRequestDTO dto) {
        String titulo = dto != null ? dto.getTitulo() : null;
        String mensaje = dto != null ? dto.getMensaje() : null;
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearDebug(titulo, mensaje));
    }
}
