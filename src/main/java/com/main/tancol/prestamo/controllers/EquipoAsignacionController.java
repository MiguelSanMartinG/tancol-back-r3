package com.main.tancol.prestamo.controllers;

import com.main.tancol.prestamo.dto.request.EquipoAsignacionRequestDTO;
import com.main.tancol.prestamo.dto.response.EquipoAsignacionResponseDTO;
import com.main.tancol.prestamo.dto.response.EquipoCoberturaRevisionResponseDTO;
import com.main.tancol.prestamo.service.EquipoAsignacionService;
import com.main.tancol.prestamo.service.EquipoCoberturaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/equipo-asignacion")
@Tag(name = "Asignación de Equipo")
public class EquipoAsignacionController {

    private final EquipoAsignacionService service;
    private final EquipoCoberturaService coberturaService;

    public EquipoAsignacionController(EquipoAsignacionService service,
                                     EquipoCoberturaService coberturaService) {
        this.service = service;
        this.coberturaService = coberturaService;
    }

    @PostMapping
    public ResponseEntity<EquipoAsignacionResponseDTO> create(@Valid @RequestBody EquipoAsignacionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<EquipoAsignacionResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(service.getAll(params));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipoAsignacionResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}/devolver")
    public ResponseEntity<EquipoAsignacionResponseDTO> devolver(@PathVariable UUID id) {
        return ResponseEntity.ok(service.devolver(id));
    }

    @PostMapping("/revisar-cobertura")
    @Operation(summary = "Revisión manual de cobertura semanal (misma lógica del cron dominical)")
    public ResponseEntity<EquipoCoberturaRevisionResponseDTO> revisarCobertura() {
        return ResponseEntity.ok(coberturaService.revisarCoberturaSemanal());
    }
}
