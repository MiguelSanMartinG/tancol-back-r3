package com.main.tancol.tabuladorrotos.controller;

import com.main.tancol.tabuladorrotos.dto.request.TabuladorBotellonRotoRequestDTO;
import com.main.tancol.tabuladorrotos.dto.response.TabuladorBotellonRotoResponseDTO;
import com.main.tancol.tabuladorrotos.service.TabuladorBotellonRotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/tabulador-botellones-rotos")
@Tag(name = "Tabulador de botellones rotos")
public class TabuladorBotellonRotoController {

    private final TabuladorBotellonRotoService service;

    public TabuladorBotellonRotoController(TabuladorBotellonRotoService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Alta de tolerancia semanal de botellones rotos por empleado y producto")
    public ResponseEntity<TabuladorBotellonRotoResponseDTO> create(
            @Valid @RequestBody TabuladorBotellonRotoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<TabuladorBotellonRotoResponseDTO>> findAll(
            @RequestParam Map<String, String> params) {
        return ResponseEntity.ok(service.getAll(params));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TabuladorBotellonRotoResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TabuladorBotellonRotoResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody TabuladorBotellonRotoRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TabuladorBotellonRotoResponseDTO> delete(@PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }
}
