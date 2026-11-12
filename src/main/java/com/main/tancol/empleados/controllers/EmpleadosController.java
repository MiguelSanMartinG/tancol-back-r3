package com.main.tancol.empleados.controllers;

import com.main.tancol.empleados.dto.request.EmpleadosRequestDTO;
import com.main.tancol.empleados.dto.response.EmpleadosResponseDTO;
import com.main.tancol.empleados.service.EmpleadosService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@RestController
@RequestMapping("/empleados")
@Tag(name = "👨‍🏭 Empleados")
public class EmpleadosController {

    private final EmpleadosService service;


    public EmpleadosController(EmpleadosService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<EmpleadosResponseDTO> create(@Valid @RequestBody EmpleadosRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<EmpleadosResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<EmpleadosResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpleadosResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpleadosResponseDTO> update(@PathVariable UUID id, @RequestBody EmpleadosRequestDTO dto) {
        return ResponseEntity.ok(service.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<EmpleadosResponseDTO> delete(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }

}
