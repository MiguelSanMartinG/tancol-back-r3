package com.main.tancol.cliente.controllers;

import com.main.tancol.cliente.dto.request.TiposClientesRequestDTO;
import com.main.tancol.cliente.dto.response.TiposClientesResponseDTO;
import com.main.tancol.cliente.service.TiposClientesService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@RestController
@RequestMapping("/tipo-cliente")
@Tag(name = "❇️ Tipo de Cliente")
public class TiposClientesController {

    private final TiposClientesService service;


    public TiposClientesController(TiposClientesService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TiposClientesResponseDTO> create(@Valid @RequestBody TiposClientesRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<TiposClientesResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<TiposClientesResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TiposClientesResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TiposClientesResponseDTO> update(@PathVariable UUID id, @RequestBody TiposClientesRequestDTO dto) {
        return ResponseEntity.ok(service.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TiposClientesResponseDTO> delete(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }

}
