package com.main.tancol.almacen.controller.partidas;

import com.main.tancol.almacen.dto.request.partidas.PartidaOrdenInternaRequestDTO;
import com.main.tancol.almacen.dto.response.partidas.PartidaOrdenInternaResponseDTO;
import com.main.tancol.almacen.service.PartidaOrdenInternaService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@RestController
@Hidden
@RequestMapping("/PartidaOrdenInterna")
@Tag(name = "Undefinited")
public class PartidaOrdenInternaController {

    private final PartidaOrdenInternaService service;


    public PartidaOrdenInternaController(PartidaOrdenInternaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PartidaOrdenInternaResponseDTO> create(@Valid @RequestBody PartidaOrdenInternaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<PartidaOrdenInternaResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<PartidaOrdenInternaResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartidaOrdenInternaResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PartidaOrdenInternaResponseDTO> update(@PathVariable UUID id, @RequestBody PartidaOrdenInternaRequestDTO dto) {
        return ResponseEntity.ok(service.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PartidaOrdenInternaResponseDTO> delete(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }

}
