package com.main.tancol.liquidacion.controller;
import com.main.tancol.liquidacion.dto.request.ConceptosVentasRequestDTO;
import com.main.tancol.liquidacion.dto.response.*;
import com.main.tancol.liquidacion.service.ConceptosVentasService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@RestController
@RequestMapping("/conceptos-ventas")
@Tag(name = "💠 Conceptos ventas")
public class ConceptosVentasController {

    private final ConceptosVentasService service;


    public ConceptosVentasController(ConceptosVentasService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ConceptosVentasResponseDTO> create(@Valid @RequestBody ConceptosVentasRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<ConceptosVentasResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<ConceptosVentasResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConceptosVentasResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConceptosVentasResponseDTO> update(@PathVariable UUID id, @RequestBody ConceptosVentasRequestDTO dto) {
        return ResponseEntity.ok(service.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ConceptosVentasResponseDTO> delete(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }

}
