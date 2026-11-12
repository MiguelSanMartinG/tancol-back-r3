package com.main.tancol.producto.controller.comercial;

import com.main.tancol.producto.dto.request.comercial.CategoriaProductoComercialRequestDTO;
import com.main.tancol.producto.dto.response.comercial.CategoriaProductoComercialResponseDTO;
import com.main.tancol.producto.service.comercial.CategoriaProductoComercialService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/categoria-producto-comerciales")
@Tag(name = "🫎 Categoría Producto Comerciales")
public class CategoriaProductoComercialController {

    private final CategoriaProductoComercialService service;


    public CategoriaProductoComercialController(CategoriaProductoComercialService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CategoriaProductoComercialResponseDTO> create(@Valid @RequestBody CategoriaProductoComercialRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<CategoriaProductoComercialResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<CategoriaProductoComercialResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaProductoComercialResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaProductoComercialResponseDTO> update(@PathVariable UUID id, @RequestBody CategoriaProductoComercialRequestDTO dto) {
        return ResponseEntity.ok(service.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CategoriaProductoComercialResponseDTO> delete(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }

}
