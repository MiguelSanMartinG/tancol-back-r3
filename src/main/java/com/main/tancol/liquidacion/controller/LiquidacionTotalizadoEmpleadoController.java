package com.main.tancol.liquidacion.controller;

import com.main.tancol.liquidacion.dto.request.LiquidacionTotalizadoEmpleadoRequestDTO;
import com.main.tancol.liquidacion.dto.response.LiquidacionTotalizadoEmpleadoResponseDTO;
import com.main.tancol.liquidacion.service.LiquidacionTotalizadoEmpleadoService;
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
@RequestMapping("/EmpleadoTotalizadoLiquidacion")
@Tag(name = "Undefinited")
@Hidden
public class LiquidacionTotalizadoEmpleadoController {

    private final LiquidacionTotalizadoEmpleadoService service;


    public LiquidacionTotalizadoEmpleadoController(LiquidacionTotalizadoEmpleadoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<LiquidacionTotalizadoEmpleadoResponseDTO> create(@Valid @RequestBody LiquidacionTotalizadoEmpleadoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<LiquidacionTotalizadoEmpleadoResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<LiquidacionTotalizadoEmpleadoResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LiquidacionTotalizadoEmpleadoResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LiquidacionTotalizadoEmpleadoResponseDTO> update(@PathVariable UUID id, @RequestBody LiquidacionTotalizadoEmpleadoRequestDTO dto) {
        return ResponseEntity.ok(service.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<LiquidacionTotalizadoEmpleadoResponseDTO> delete(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }

}
