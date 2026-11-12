package com.main.tancol.producto.controller;

import com.main.tancol.producto.dto.request.CategoriaRequestDTO;
import com.main.tancol.producto.dto.request.ProductoRequestDTO;
import com.main.tancol.producto.dto.response.CategoriaResponseDTO;
import com.main.tancol.producto.dto.response.ProductoResponseDTO;
import com.main.tancol.producto.service.ProductoService;
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
@RequestMapping("/producto")
@RestController
@Tag(name = "\uD83D\uDCE6 Producto")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService productoService) {
        this.service = productoService;
    }

    @PostMapping
    public ResponseEntity<ProductoResponseDTO> create(@Valid @RequestBody ProductoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<ProductoResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> update(@RequestParam @PathVariable UUID id, @Valid @RequestBody ProductoRequestDTO dto) {
        return ResponseEntity.ok(service.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> delete(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }
}