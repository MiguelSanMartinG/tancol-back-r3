package com.main.tancol.producto.controller;

import com.main.tancol.producto.dto.request.CategoriaRequestDTO;
import com.main.tancol.producto.dto.response.CategoriaResponseDTO;
import com.main.tancol.producto.service.CategoriaService;
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
@RequestMapping("/category")
@RestController
@Tag(name = "\uD83D\uDC32 Categorias")
public class CategoriaController {

    private final CategoriaService service;


    public CategoriaController(CategoriaService categoriaProducto) {
        this.service = categoriaProducto;
    }

    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> create(@Valid @RequestBody CategoriaRequestDTO categoriaProducto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.createCategoria(categoriaProducto));
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<CategoriaResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.readByID(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> update(@PathVariable UUID id, @RequestBody CategoriaRequestDTO dto) {
        return ResponseEntity.ok(service.updateCategoria(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> delete(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(service.deleteCategoria(id));
    }


}
