package com.main.tancol.prestamo.controllers;

import com.main.tancol.prestamo.dto.request.TipoEquipoRequestDTO;
import com.main.tancol.prestamo.dto.response.TipoEquipoResponseDTO;
import com.main.tancol.prestamo.service.TipoEquipoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/tipo-equipo")
@Tag(name = "Tipo de Equipo")
public class TipoEquipoController {

    private final TipoEquipoService service;

    public TipoEquipoController(TipoEquipoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TipoEquipoResponseDTO> create(@Valid @RequestBody TipoEquipoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<TipoEquipoResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(service.getAll(params));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoEquipoResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoEquipoResponseDTO> update(@PathVariable UUID id, @RequestBody TipoEquipoRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TipoEquipoResponseDTO> delete(@PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }
}
