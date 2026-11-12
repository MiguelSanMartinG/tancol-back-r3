package com.main.tancol.producto.controller.insumos;

import com.main.tancol.producto.dto.request.insumo.CategoriaRequestDTO;
import com.main.tancol.producto.dto.response.insumos.CategoriaExistenciaResponseDTO;
import com.main.tancol.producto.dto.response.insumos.CategoriaProductosResponseDTO;
import com.main.tancol.producto.dto.response.insumos.CategoriaRecuentoResponseDTO;
import com.main.tancol.producto.dto.response.insumos.CategoriaResponseDTO;
import com.main.tancol.producto.service.insumo.CategoriaService;
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
@Tag(name = "\uD83D\uDC32 Categoría Producto Basico")
public class CategoriaProductoBasicoController {

    private final CategoriaService service;


    public CategoriaProductoBasicoController(CategoriaService categoriaProducto) {
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

    @GetMapping("/{id}/productos")
    public ResponseEntity<CategoriaProductosResponseDTO> productos(@PathVariable UUID id) {
        return ResponseEntity.ok(service.productos(id));
    }

    @GetMapping("/{id}/recuento")
    public ResponseEntity<CategoriaRecuentoResponseDTO> recuento(@PathVariable UUID id) {
        return ResponseEntity.ok(service.recuento(id));
    }

    @GetMapping("/{id}/existencia")
    public ResponseEntity<CategoriaExistenciaResponseDTO> existencia(@PathVariable UUID id) {
        return ResponseEntity.ok(service.existencia(id));
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
