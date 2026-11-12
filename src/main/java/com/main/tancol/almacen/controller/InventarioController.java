package com.main.tancol.almacen.controller;


import com.main.tancol.almacen.service.InventarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import com.main.tancol.almacen.dto.response.*;
import com.main.tancol.almacen.dto.request.*;


import java.util.*;

@RestController
@RequestMapping("/inventario")
@Tag(name = "🐦 Inventarios")
public class InventarioController {

    private final InventarioService service;


    public InventarioController(InventarioService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<InventarioResponseDTO> create(@Valid @RequestBody InventarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<InventarioResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<InventarioResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventarioResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventarioResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody InventarioRequestDTO dto) {
        return ResponseEntity.ok(service.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String,String>> delete(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(service.borrarPermanentemente(id));
    }

}
