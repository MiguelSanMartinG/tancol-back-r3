package com.main.tancol.empleados.controllers;

import com.main.tancol.empleados.dto.request.TiposEmpleadosRequestDTO;
import com.main.tancol.empleados.dto.response.TiposEmpleadosResponseDTO;
import com.main.tancol.empleados.service.TiposEmpleadosService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@RestController
@RequestMapping("/tipo-empleado")
@Tag(name = "*️⃣ Tipo de Empleado")
public class TiposEmpleadosController {

    private final TiposEmpleadosService service;


    public TiposEmpleadosController(TiposEmpleadosService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TiposEmpleadosResponseDTO> create(@Valid @RequestBody TiposEmpleadosRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<TiposEmpleadosResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<TiposEmpleadosResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TiposEmpleadosResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TiposEmpleadosResponseDTO> update(@PathVariable UUID id, @RequestBody TiposEmpleadosRequestDTO dto) {
        return ResponseEntity.ok(service.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TiposEmpleadosResponseDTO> delete(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }

}
