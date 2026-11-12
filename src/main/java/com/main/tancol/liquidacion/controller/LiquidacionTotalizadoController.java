package com.main.tancol.liquidacion.controller;

import com.main.tancol.liquidacion.dto.request.LiquidacionTotalizadoRequestDTO;
import com.main.tancol.liquidacion.dto.response.LiquidacionTotalizadoResponseDTO;
import com.main.tancol.liquidacion.service.LiquidacionTotalizadoService;
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
@RequestMapping("/TotalizadoChoferes")
@Tag(name = "Undefinited")
@Hidden
public class LiquidacionTotalizadoController {

    private final LiquidacionTotalizadoService service;


    public LiquidacionTotalizadoController(LiquidacionTotalizadoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<LiquidacionTotalizadoResponseDTO> create(@Valid @RequestBody LiquidacionTotalizadoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<LiquidacionTotalizadoResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<LiquidacionTotalizadoResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LiquidacionTotalizadoResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LiquidacionTotalizadoResponseDTO> update(@PathVariable UUID id, @RequestBody LiquidacionTotalizadoRequestDTO dto) {
        return ResponseEntity.ok(service.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<LiquidacionTotalizadoResponseDTO> delete(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }

}
