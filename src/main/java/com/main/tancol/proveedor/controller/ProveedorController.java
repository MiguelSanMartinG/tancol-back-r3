package com.main.tancol.proveedor.controller;
import com.main.tancol.common.dto.PageResponse;
import com.main.tancol.proveedor.dto.request.ProveedorRequestDTO;
import com.main.tancol.proveedor.dto.response.ProveedorResponseDTO;
import com.main.tancol.proveedor.service.ProveedorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequestMapping("/proveedor")
@RestController
@Tag(name = "\uD83D\uDC77 Proveedor")
public class ProveedorController {

    private final ProveedorService service;

    public ProveedorController(ProveedorService proveedorService) {
        this.service = proveedorService;
    }

    @PostMapping
    public ResponseEntity<ProveedorResponseDTO> create(@Valid @RequestBody ProveedorRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<ProveedorResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<ProveedorResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/page")
    public ResponseEntity<PageResponse<ProveedorResponseDTO>> page(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(this.service.getPage(params));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProveedorResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody ProveedorRequestDTO dto) {
        return ResponseEntity.ok(service.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProveedorResponseDTO> delete(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }

}
