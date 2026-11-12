package com.main.tancol.proveedor.controller;

import com.main.tancol.proveedor.dto.request.TipoProveedorRequestDTO;
import com.main.tancol.proveedor.dto.response.TipoProveedorResponseDTO;
import com.main.tancol.proveedor.service.TipoProveedorService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@RestController
@RequestMapping("/tipo-proveedor")
@Tag(name = "🏪 Tipo de Proveedores")
public class TipoProveedorController {

    private final TipoProveedorService service;


    public TipoProveedorController(TipoProveedorService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TipoProveedorResponseDTO> create(@Valid @RequestBody TipoProveedorRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<TipoProveedorResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<TipoProveedorResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoProveedorResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoProveedorResponseDTO> update(@PathVariable UUID id, @RequestBody TipoProveedorRequestDTO dto) {
        return ResponseEntity.ok(service.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TipoProveedorResponseDTO> delete(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }

}
