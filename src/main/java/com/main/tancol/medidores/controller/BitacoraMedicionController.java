package com.main.tancol.medidores.controller;

import com.main.tancol.medidores.dto.request.BitacoraMedicionRequestDTO;
import com.main.tancol.medidores.dto.response.BitacoraMedicionResponseDTO;
import com.main.tancol.medidores.service.BitacoraMedicionService;
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
@RequestMapping("/bitacora-medicion")
@Tag(name = "📏 Bitacora Mediciones")
public class BitacoraMedicionController {

    private final BitacoraMedicionService service;


    public BitacoraMedicionController(BitacoraMedicionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<BitacoraMedicionResponseDTO> create(@Valid @RequestBody BitacoraMedicionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<BitacoraMedicionResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<BitacoraMedicionResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BitacoraMedicionResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    @Hidden
    public ResponseEntity<BitacoraMedicionResponseDTO> update(@PathVariable UUID id, @RequestBody BitacoraMedicionRequestDTO dto) {
        return ResponseEntity.ok(service.update(id,dto));
    }

    @DeleteMapping("/{id}")
    @Hidden
    public ResponseEntity<BitacoraMedicionResponseDTO> delete(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }

}
