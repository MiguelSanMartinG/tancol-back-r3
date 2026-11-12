package com.main.tancol.logistica.controllers;

import com.main.tancol.logistica.dto.request.TrayectoRequestDTO;
import com.main.tancol.logistica.dto.response.TrayectoResponseDTO;
import com.main.tancol.logistica.service.bitacoras.TrayectoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/viaje")
@Tag(name = "\uD83E\uDDED Viajes")
public class TrayectoController {

    private final TrayectoService service;


    public TrayectoController(TrayectoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TrayectoResponseDTO> create(@Valid @RequestBody TrayectoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<TrayectoResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<TrayectoResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrayectoResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrayectoResponseDTO> update(@PathVariable UUID id, @RequestBody TrayectoRequestDTO dto) {
        return ResponseEntity.ok(service.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }

}
