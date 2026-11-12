package com.main.tancol.tablasamonestaciones.controller;

import com.main.tancol.tablasamonestaciones.dto.request.TablasAmonestacionesRequestDTO;
import com.main.tancol.tablasamonestaciones.dto.response.ProductoAmonestacionDisponibleResponseDTO;
import com.main.tancol.tablasamonestaciones.dto.response.TablasAmonestacionesResponseDTO;
import com.main.tancol.tablasamonestaciones.service.TablasAmonestacionesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/tablas-amonestaciones")
@Tag(name = "Tablas de amonestaciones")
public class TablasAmonestacionesController {

    private final TablasAmonestacionesService service;

    public TablasAmonestacionesController(TablasAmonestacionesService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Alta de una tabla de amonestación por producto Tancol")
    public ResponseEntity<TablasAmonestacionesResponseDTO> create(
            @Valid @RequestBody TablasAmonestacionesRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<TablasAmonestacionesResponseDTO>> findAll(
            @RequestParam Map<String, String> params) {
        return ResponseEntity.ok(service.getAll(params));
    }

    @GetMapping("/productos-disponibles")
    @Operation(summary = "Productos Tancol que todavía no tienen una tabla de amonestación")
    public ResponseEntity<List<ProductoAmonestacionDisponibleResponseDTO>> productosDisponibles(
            @RequestParam(required = false) UUID tablaId) {
        return ResponseEntity.ok(service.productosDisponibles(tablaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TablasAmonestacionesResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TablasAmonestacionesResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody TablasAmonestacionesRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TablasAmonestacionesResponseDTO> delete(@PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }
}
