package com.main.tancol.logistica.controllers;

import com.main.tancol.logistica.dto.request.ProductosFueraEspecificacionRequestDTO;
import com.main.tancol.logistica.dto.response.ProductosFueraEspecificacionResponseDTO;
import com.main.tancol.logistica.service.ProductosFueraEspecificacionService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/ProductosFueraEspecificacion")
@Tag(name = "Undefinited")
@Hidden
public class ProductosFueraEspecificacionController {

    private final ProductosFueraEspecificacionService service;


    public ProductosFueraEspecificacionController(ProductosFueraEspecificacionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ProductosFueraEspecificacionResponseDTO> create(@Valid @RequestBody ProductosFueraEspecificacionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<ProductosFueraEspecificacionResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<ProductosFueraEspecificacionResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductosFueraEspecificacionResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductosFueraEspecificacionResponseDTO> update(@PathVariable UUID id, @RequestBody ProductosFueraEspecificacionRequestDTO dto) {
        return ResponseEntity.ok(service.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductosFueraEspecificacionResponseDTO> delete(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }

}
