package com.main.tancol.logistica.controllers;

import com.main.tancol.logistica.dto.request.RutasRequestDTO;
import com.main.tancol.logistica.dto.response.RutasResponseDTO;
import com.main.tancol.logistica.service.rutas.RutasService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/rutas")
@Tag(name = "🛣️ Rutas")
public class RutasController {

    private final RutasService service;


    public RutasController(RutasService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<RutasResponseDTO> create(@Valid @RequestBody RutasRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<RutasResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<RutasResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RutasResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RutasResponseDTO> update(@PathVariable UUID id, @RequestBody RutasRequestDTO dto) {
        return ResponseEntity.ok(service.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RutasResponseDTO> delete(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }

}
