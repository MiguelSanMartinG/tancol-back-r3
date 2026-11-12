package com.main.tancol.liquidacion.controller;

import com.main.tancol.liquidacion.dto.request.LiquidacionPartidaRequestDTO;
import com.main.tancol.liquidacion.dto.response.LiquidacionPartidaResponseDTO;
import com.main.tancol.liquidacion.service.LiquidacionPartidaService;
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
@RequestMapping("/liquidacion-partida")
@Tag(name = "💰 Liquidaciones Partida")
@Hidden
public class LiquidacionPartidaController {

    private final LiquidacionPartidaService service;


    public LiquidacionPartidaController(LiquidacionPartidaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<LiquidacionPartidaResponseDTO> create(@Valid @RequestBody LiquidacionPartidaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<LiquidacionPartidaResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<LiquidacionPartidaResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LiquidacionPartidaResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LiquidacionPartidaResponseDTO> update(@PathVariable UUID id, @RequestBody LiquidacionPartidaRequestDTO dto) {
        return ResponseEntity.ok(service.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<LiquidacionPartidaResponseDTO> delete(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }

}
