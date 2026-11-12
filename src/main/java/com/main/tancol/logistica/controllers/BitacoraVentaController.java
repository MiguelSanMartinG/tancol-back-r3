package com.main.tancol.logistica.controllers;

import com.main.tancol.logistica.dto.request.BitacoraVentaRequestDTO;
import com.main.tancol.logistica.dto.response.BitacoraVentaResponseDTO;
import com.main.tancol.logistica.service.bitacoras.BitacoraVentaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/bitacoras-rutas")
@Tag(name = "🚛 Bitacoras de Ventas por Rutas")
public class BitacoraVentaController {

    private final BitacoraVentaService service;


    public BitacoraVentaController(BitacoraVentaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<BitacoraVentaResponseDTO> create(@Valid @RequestBody BitacoraVentaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<BitacoraVentaResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<BitacoraVentaResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/fecha/{check}")
    public ResponseEntity<?> getByFecha(@PathVariable LocalDate check) {
        return service.findByFecha(check);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BitacoraVentaResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BitacoraVentaResponseDTO> update(@PathVariable UUID id, @RequestBody BitacoraVentaRequestDTO dto) {
        return ResponseEntity.ok(service.update(id,dto));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }


}
