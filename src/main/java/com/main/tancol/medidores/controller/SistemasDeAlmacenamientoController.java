package com.main.tancol.medidores.controller;

import com.main.tancol.medidores.dto.request.SistemasDeAlmacenamientoRequestDTO;
import com.main.tancol.medidores.dto.response.SistemasDeAlmacenamientoResponseDTO;
import com.main.tancol.medidores.service.SistemasDeAlmacenamientoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/sistemas-almacenamiento")
@Tag(name = "🚰 Sistemas de Almacenamiento")
public class SistemasDeAlmacenamientoController {

    private final SistemasDeAlmacenamientoService service;


    public SistemasDeAlmacenamientoController(SistemasDeAlmacenamientoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SistemasDeAlmacenamientoResponseDTO> create(@Valid @RequestBody SistemasDeAlmacenamientoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<SistemasDeAlmacenamientoResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<SistemasDeAlmacenamientoResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SistemasDeAlmacenamientoResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SistemasDeAlmacenamientoResponseDTO> update(@PathVariable UUID id, @RequestBody SistemasDeAlmacenamientoRequestDTO dto) {
        return ResponseEntity.ok(service.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SistemasDeAlmacenamientoResponseDTO> delete(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }

}
