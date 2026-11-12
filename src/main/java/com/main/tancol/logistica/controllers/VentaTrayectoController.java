package com.main.tancol.logistica.controllers;

import com.main.tancol.logistica.dto.request.VentaTrayectoRequestDTO;
import com.main.tancol.logistica.dto.response.VentaTrayectoResponseDTO;
import com.main.tancol.logistica.service.bitacoras.VentaTrayectoService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/VentaTrayecto")
@Tag(name = "Undefinited")
@Hidden
public class VentaTrayectoController {

    private final VentaTrayectoService service;


    public VentaTrayectoController(VentaTrayectoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<VentaTrayectoResponseDTO> create(@Valid @RequestBody VentaTrayectoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<VentaTrayectoResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<VentaTrayectoResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaTrayectoResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VentaTrayectoResponseDTO> update(@PathVariable UUID id, @RequestBody VentaTrayectoRequestDTO dto) {
        return ResponseEntity.ok(service.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<VentaTrayectoResponseDTO> delete(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }

}
