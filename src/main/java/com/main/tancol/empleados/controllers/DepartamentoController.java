package com.main.tancol.empleados.controllers;

import com.main.tancol.empleados.dto.request.DepartamentoRequestDTO;
import com.main.tancol.empleados.dto.response.DepartamentoResponseDTO;
import com.main.tancol.empleados.service.DepartamentoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@RestController
@RequestMapping("/departamento")
@Tag(name = "🪹 Departamento")
public class DepartamentoController {

    private final DepartamentoService service;


    public DepartamentoController(DepartamentoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DepartamentoResponseDTO> create(@Valid @RequestBody DepartamentoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<DepartamentoResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<DepartamentoResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartamentoResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartamentoResponseDTO> update(@PathVariable UUID id, @RequestBody DepartamentoRequestDTO dto) {
        return ResponseEntity.ok(service.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DepartamentoResponseDTO> delete(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }

}
