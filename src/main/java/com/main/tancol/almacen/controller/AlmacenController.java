package com.main.tancol.almacen.controller;
import com.main.tancol.almacen.dto.request.AlmacenRequestDTO;
import com.main.tancol.common.dto.PageResponse;
import com.main.tancol.almacen.dto.response.AlmacenResponseDTO;
import com.main.tancol.almacen.service.AlmacenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequestMapping("/almacen")
@RestController
@Tag(name = "\uD83D\uDDC3\uFE0F Almacen")
@Slf4j
public class AlmacenController {

    private final AlmacenService almacenService;
    public AlmacenController(AlmacenService almacenService) {
        this.almacenService = almacenService;
    }

    @PostMapping
    public ResponseEntity<AlmacenResponseDTO> create(@Valid @RequestBody AlmacenRequestDTO almacen) {
        return ResponseEntity.status(HttpStatus.CREATED).body(almacenService.createAlmacen(almacen));
    }


    @GetMapping
    public ResponseEntity<List<AlmacenResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<AlmacenResponseDTO> response = almacenService.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/page")
    public ResponseEntity<PageResponse<AlmacenResponseDTO>> page(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(almacenService.getPage(params));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlmacenResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(almacenService.readByID(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody AlmacenRequestDTO almacen) {
        return ResponseEntity.ok(almacenService.updateAlmacen(id,almacen));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(almacenService.deleteAlmacen(id));
    }

}
