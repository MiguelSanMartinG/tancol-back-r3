package com.main.tancol.orden.controllers;

import com.main.tancol.orden.dto.request.PartidaOrdenRequestDTO;
import com.main.tancol.orden.dto.response.PartidaOrdenResponseDTO;
import com.main.tancol.orden.entities.PartidaOrdenEntity;
import com.main.tancol.orden.service.PartidaOrdenService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@RestController
@RequestMapping("/api/PartidaCompra")
@Hidden
@Tag(name = "PartidadeOrden")
public class PartidaOrdenController {

    private final PartidaOrdenService service;


    public PartidaOrdenController(PartidaOrdenService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PartidaOrdenEntity> create(@Valid @RequestBody PartidaOrdenRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto, null));
    }

    @GetMapping
    public ResponseEntity<List<PartidaOrdenResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<PartidaOrdenResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartidaOrdenResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PartidaOrdenResponseDTO> update(@PathVariable UUID id, @RequestBody PartidaOrdenRequestDTO dto) {
        return ResponseEntity.ok(service.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PartidaOrdenResponseDTO> delete(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }

}
