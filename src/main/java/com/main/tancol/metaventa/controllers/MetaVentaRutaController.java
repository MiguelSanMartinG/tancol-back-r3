package com.main.tancol.metaventa.controllers;

import com.main.tancol.metaventa.dto.request.MetaVentaRutaRequestDTO;
import com.main.tancol.metaventa.dto.request.MetaVentaUpdateRequestDTO;
import com.main.tancol.metaventa.dto.response.MetaVentaRutaResponseDTO;
import com.main.tancol.metaventa.service.MetaVentaRutaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/meta-venta-ruta")
@Tag(name = "Metas de ventas por ruta")
public class MetaVentaRutaController {

    private final MetaVentaRutaService service;

    public MetaVentaRutaController(MetaVentaRutaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<MetaVentaRutaResponseDTO> create(@Valid @RequestBody MetaVentaRutaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<MetaVentaRutaResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(service.getAll(params));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetaVentaRutaResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetaVentaRutaResponseDTO> update(@PathVariable UUID id,
                                                           @Valid @RequestBody MetaVentaUpdateRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @PutMapping("/{id}/cerrar")
    public ResponseEntity<MetaVentaRutaResponseDTO> cerrar(@PathVariable UUID id) {
        return ResponseEntity.ok(service.cerrar(id));
    }
}
