package com.main.tancol.producto.controller.comercial;

import com.main.tancol.common.dto.PageResponse;
import com.main.tancol.producto.dto.request.comercial.ProductoComercialRequestDTO;
import com.main.tancol.producto.dto.response.comercial.ProductoComercialResponseDTO;
import com.main.tancol.producto.service.comercial.ProductoComercialService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/producto-comercial")
@Tag(name = "🛒 Producto Tancol")
public class ProductoComercialController {

    private final ProductoComercialService service;


    public ProductoComercialController(ProductoComercialService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ProductoComercialResponseDTO> create(@Valid @RequestBody ProductoComercialRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<ProductoComercialResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<ProductoComercialResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/page")
    public ResponseEntity<PageResponse<ProductoComercialResponseDTO>> page(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(service.getPage(params));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoComercialResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoComercialResponseDTO> update(@PathVariable UUID id, @RequestBody ProductoComercialRequestDTO dto) {
        return ResponseEntity.ok(service.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductoComercialResponseDTO> delete(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }

}
