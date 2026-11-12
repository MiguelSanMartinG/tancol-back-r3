package com.main.tancol.permisos.controller;

import com.main.tancol.permisos.dto.request.AsignacionPermisoAvanzadoRequestDTO;
import com.main.tancol.permisos.dto.request.PermisoAvanzadoRequestDTO;
import com.main.tancol.permisos.dto.response.AsignacionPermisoAvanzadoResponseDTO;
import com.main.tancol.permisos.dto.response.PermisoAvanzadoResponseDTO;
import com.main.tancol.permisos.dto.response.TienePermisoAvanzadoResponseDTO;
import com.main.tancol.permisos.service.PermisoAvanzadoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/permisos-avanzados")
@RequiredArgsConstructor
@Tag(name = "Permisos avanzados")
public class PermisoAvanzadoController {

    private final PermisoAvanzadoService service;

    @GetMapping
    public ResponseEntity<List<PermisoAvanzadoResponseDTO>> listarCatalogo() {
        return ResponseEntity.ok(service.listarCatalogo());
    }

    @PostMapping
    public ResponseEntity<PermisoAvanzadoResponseDTO> crearPermiso(
            @Valid @RequestBody PermisoAvanzadoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearPermiso(dto));
    }

    @GetMapping("/asignaciones")
    public ResponseEntity<List<AsignacionPermisoAvanzadoResponseDTO>> listarAsignaciones(
            @RequestParam(required = false) UUID empleadoId) {
        return ResponseEntity.ok(service.listarAsignaciones(empleadoId));
    }

    @PostMapping("/asignaciones")
    public ResponseEntity<AsignacionPermisoAvanzadoResponseDTO> asignar(
            @Valid @RequestBody AsignacionPermisoAvanzadoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.asignar(dto));
    }

    @DeleteMapping("/asignaciones/{id}")
    public ResponseEntity<Void> quitarAsignacion(@PathVariable UUID id) {
        service.quitarAsignacion(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<List<PermisoAvanzadoResponseDTO>> misPermisos() {
        return ResponseEntity.ok(service.misPermisos());
    }

    @GetMapping("/me/tiene/{codigo}")
    public ResponseEntity<TienePermisoAvanzadoResponseDTO> tengoPermiso(@PathVariable String codigo) {
        return ResponseEntity.ok(service.tengoPermiso(codigo));
    }
}
