package com.main.tancol.almacen.controller;

import com.main.tancol.almacen.dto.request.UnidadesMedidaRequestDTO;
import com.main.tancol.almacen.dto.response.UnidadesMedidaResponseDTO;
import com.main.tancol.almacen.service.UnidadesMedidaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@RestController
@RequestMapping("/unidad-medida")
@Tag(name = "🧊 Unidad de medida")
public class UnidadesMedidaController {

    private final UnidadesMedidaService service;


    public UnidadesMedidaController(UnidadesMedidaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UnidadesMedidaResponseDTO> create(@Valid @RequestBody UnidadesMedidaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<UnidadesMedidaResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<UnidadesMedidaResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UnidadesMedidaResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UnidadesMedidaResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody UnidadesMedidaRequestDTO dto) {
        return ResponseEntity.ok(service.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UnidadesMedidaResponseDTO> delete(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }

}
