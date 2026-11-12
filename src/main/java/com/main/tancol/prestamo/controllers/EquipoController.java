package com.main.tancol.prestamo.controllers;

import com.main.tancol.common.dto.PageResponse;
import com.main.tancol.prestamo.dto.request.EquipoRequestDTO;
import com.main.tancol.prestamo.dto.response.EquipoResponseDTO;
import com.main.tancol.prestamo.service.EquipoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/equipo")
@Tag(name = "Equipo")
public class EquipoController {

    private final EquipoService service;

    public EquipoController(EquipoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<EquipoResponseDTO> create(@Valid @RequestBody EquipoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<EquipoResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(service.getAll(params));
    }

    @GetMapping("/page")
    public ResponseEntity<PageResponse<EquipoResponseDTO>> page(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(service.getPage(params));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipoResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EquipoResponseDTO> update(@PathVariable UUID id, @RequestBody EquipoRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<EquipoResponseDTO> delete(@PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }
}
