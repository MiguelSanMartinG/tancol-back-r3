package com.main.tancol.metaventa.controllers;

import com.main.tancol.metaventa.dto.request.MetaVentaRequestDTO;
import com.main.tancol.metaventa.dto.request.MetaVentaUpdateRequestDTO;
import com.main.tancol.metaventa.dto.response.MetaVentaCoberturaRevisionResponseDTO;
import com.main.tancol.metaventa.dto.response.MetaVentaResponseDTO;
import com.main.tancol.metaventa.service.MetaVentaCoberturaService;
import com.main.tancol.metaventa.service.MetaVentaService;
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
@RequestMapping("/meta-venta")
@Tag(name = "Metas de ventas")
public class MetaVentaController {

    private final MetaVentaService service;
    private final MetaVentaCoberturaService coberturaService;

    public MetaVentaController(MetaVentaService service, MetaVentaCoberturaService coberturaService) {
        this.service = service;
        this.coberturaService = coberturaService;
    }

    @PostMapping
    public ResponseEntity<MetaVentaResponseDTO> create(@Valid @RequestBody MetaVentaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<MetaVentaResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(service.getAll(params));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetaVentaResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetaVentaResponseDTO> update(@PathVariable UUID id,
                                                       @Valid @RequestBody MetaVentaUpdateRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @PutMapping("/{id}/cerrar")
    public ResponseEntity<MetaVentaResponseDTO> cerrar(@PathVariable UUID id) {
        return ResponseEntity.ok(service.cerrar(id));
    }

    @PostMapping("/revisar")
    @Operation(summary = "Revisión manual de metas semanales (misma lógica del cron de viernes 13:00)")
    public ResponseEntity<MetaVentaCoberturaRevisionResponseDTO> revisar() {
        return ResponseEntity.ok(coberturaService.revisarMetasSemanales());
    }
}
