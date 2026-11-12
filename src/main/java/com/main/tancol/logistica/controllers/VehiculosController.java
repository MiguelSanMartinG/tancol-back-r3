package com.main.tancol.logistica.controllers;

import com.main.tancol.logistica.dto.request.VehiculosRequestDTO;
import com.main.tancol.logistica.dto.response.VehiculosResponseDTO;
import com.main.tancol.logistica.service.rutas.VehiculosService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/vehiculos")
@Tag(name = "🛻 Vehiculos")
public class VehiculosController {

    private final VehiculosService service;


    public VehiculosController(VehiculosService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<VehiculosResponseDTO> create(@Valid @RequestBody VehiculosRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<VehiculosResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<VehiculosResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehiculosResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehiculosResponseDTO> update(@PathVariable UUID id, @RequestBody VehiculosRequestDTO dto) {
        return ResponseEntity.ok(service.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<VehiculosResponseDTO> delete(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }

}
