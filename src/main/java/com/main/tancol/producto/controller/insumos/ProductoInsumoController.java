package com.main.tancol.producto.controller.insumos;

import com.main.tancol.producto.dto.request.insumo.ProductoRequestDTO;
import com.main.tancol.producto.dto.response.insumos.ProductoResponseDTO;
import com.main.tancol.producto.dto.response.insumos.StockMinimoRevisionResponseDTO;
import com.main.tancol.producto.service.insumo.ProductoService;
import com.main.tancol.producto.service.insumo.StockMinimoRevisionService;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "\uD83D\uDCE6 Producto Insumos")
public class ProductoInsumoController {

    private final ProductoService service;
    private final StockMinimoRevisionService stockMinimoRevisionService;

    public ProductoInsumoController(ProductoService productoService,
                                    StockMinimoRevisionService stockMinimoRevisionService) {
        this.service = productoService;
        this.stockMinimoRevisionService = stockMinimoRevisionService;
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
    public ResponseEntity<ProductoResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody ProductoRequestDTO dto) {
        return ResponseEntity.ok(service.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> delete(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }

    @PostMapping("/revisar-stock-minimo")
    @Operation(summary = "Revisión manual de insumos con stock total bajo el mínimo")
    public ResponseEntity<StockMinimoRevisionResponseDTO> revisarStockMinimo() {
        return ResponseEntity.ok(stockMinimoRevisionService.revisarStockMinimo());
    }
}