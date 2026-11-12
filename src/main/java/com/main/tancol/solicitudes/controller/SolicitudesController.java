package com.main.tancol.solicitudes.controller;

import com.main.tancol.solicitudes.dto.request.SolicitudesRequestDTO;
import com.main.tancol.solicitudes.dto.response.SolicitudesResponseDTO;
import com.main.tancol.solicitudes.service.SolicitudesService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/solicitud")
@Tag(name = "Solicitudes")
public class SolicitudesController {

    private final SolicitudesService solicitudesService;


    public SolicitudesController(SolicitudesService service) {
        this.solicitudesService = service;
    }

    @PostMapping
    public ResponseEntity<SolicitudesResponseDTO> create(@Valid @RequestBody SolicitudesRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.solicitudesService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<SolicitudesResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<SolicitudesResponseDTO> response = this.solicitudesService.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitudesResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(solicitudesService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SolicitudesResponseDTO> update(@PathVariable UUID id, @RequestBody SolicitudesRequestDTO dto) {
        return ResponseEntity.ok(solicitudesService.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SolicitudesResponseDTO> delete(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(solicitudesService.delete(id));
    }

}
