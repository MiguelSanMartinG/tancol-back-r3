package com.main.tancol.tablascomisiones.controller;

import com.main.tancol.tablascomisiones.dto.request.TablasComisionesRequestDTO;
import com.main.tancol.tablascomisiones.dto.response.ProductoComisionDisponibleResponseDTO;
import com.main.tancol.tablascomisiones.dto.response.TablasComisionesResponseDTO;
import com.main.tancol.tablascomisiones.service.TablasComisionesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/tablas-comisiones")
@Tag(name = "Tablas de comisiones")
public class TablasComisionesController {

    private final TablasComisionesService service;

    public TablasComisionesController(TablasComisionesService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Alta de una tabla de comisiones por producto comercial")
    public ResponseEntity<TablasComisionesResponseDTO> create(
            @Valid @RequestBody TablasComisionesRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<TablasComisionesResponseDTO>> findAll(
            @RequestParam Map<String, String> params) {
        return ResponseEntity.ok(service.getAll(params));
    }

    @GetMapping("/productos-disponibles")
    @Operation(summary = "Productos Tancol que todavía no tienen una tabla de comisiones")
    public ResponseEntity<List<ProductoComisionDisponibleResponseDTO>> productosDisponibles(
            @RequestParam(required = false) UUID tablaId) {
        return ResponseEntity.ok(service.productosDisponibles(tablaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TablasComisionesResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TablasComisionesResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody TablasComisionesRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TablasComisionesResponseDTO> delete(@PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }
}
