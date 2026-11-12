package com.main.tancol.producto.controller;

import com.main.tancol.producto.dto.request.ConceptosRequestDTO;
import com.main.tancol.producto.dto.response.ConceptosResponseDTO;
import com.main.tancol.producto.service.ConceptosService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@RestController
@RequestMapping("/concepto-rezago")
@Tag(name = "⛑ Conceptos Rezago")
public class ConceptosController {

    private final ConceptosService service;


    public ConceptosController(ConceptosService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ConceptosResponseDTO> create(@Valid @RequestBody ConceptosRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<ConceptosResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<ConceptosResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConceptosResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConceptosResponseDTO> update(@PathVariable Long id, @RequestBody ConceptosRequestDTO dto) {
        return ResponseEntity.ok(service.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ConceptosResponseDTO> delete(@Valid @PathVariable Long id) {
        return ResponseEntity.ok(service.delete(id));
    }

}
