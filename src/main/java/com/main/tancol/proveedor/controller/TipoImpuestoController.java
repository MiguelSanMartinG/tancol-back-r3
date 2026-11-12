package com.main.tancol.proveedor.controller;

import com.main.tancol.proveedor.dto.request.ProveedorRequestDTO;
import com.main.tancol.proveedor.dto.request.TipoImpuestoRequestDTO;
import com.main.tancol.proveedor.dto.response.TipoImpuestoResponseDTO;
import com.main.tancol.proveedor.service.TipoImpuestoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequestMapping("/tipo-impuesto")
@RestController
@Tag(name = "\uD83D\uDCB8 Tipo Impuesto")
public class TipoImpuestoController {
    private final TipoImpuestoService service;

    public TipoImpuestoController(TipoImpuestoService tipoImpuestoService) {
        this.service = tipoImpuestoService;
    }

    @PostMapping
    public ResponseEntity<TipoImpuestoResponseDTO> create(@Valid @RequestBody TipoImpuestoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<TipoImpuestoResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<TipoImpuestoResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoImpuestoResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoImpuestoResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody TipoImpuestoRequestDTO dto) {
        return ResponseEntity.ok(service.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TipoImpuestoResponseDTO> delete(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }
}
