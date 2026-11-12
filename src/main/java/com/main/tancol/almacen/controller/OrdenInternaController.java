package com.main.tancol.almacen.controller;

import com.main.tancol.almacen.dto.request.OrdenInternaRequestDTO;
import com.main.tancol.almacen.dto.response.OrdenInternaResponseDTO;
import com.main.tancol.almacen.service.OrdenInternaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@RestController
@RequestMapping("/orden-interna")
@Tag(name = "🥽 Orden Interna Material")
public class OrdenInternaController {

    private final OrdenInternaService service;


    public OrdenInternaController(OrdenInternaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<OrdenInternaResponseDTO> create(@Valid @RequestBody OrdenInternaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<OrdenInternaResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<OrdenInternaResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenInternaResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdenInternaResponseDTO> update(@PathVariable UUID id, @RequestBody OrdenInternaRequestDTO dto) {
        return ResponseEntity.ok(service.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OrdenInternaResponseDTO> delete(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }

}
