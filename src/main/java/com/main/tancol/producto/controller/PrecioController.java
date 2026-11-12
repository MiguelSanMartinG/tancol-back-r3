package com.main.tancol.producto.controller;

import com.main.tancol.producto.dto.request.PrecioRequestDTO;
import com.main.tancol.producto.dto.response.PrecioResponseDTO;

import com.main.tancol.producto.service.PrecioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequestMapping("/precio")
@RestController
@Tag(name = "\uD83D\uDCB5 Precio")
public class PrecioController {

    private final PrecioService service;

    public PrecioController(PrecioService precioService) {
        this.service = precioService;
    }

    @GetMapping
    ResponseEntity<PrecioResponseDTO> getPrecioById(@RequestParam UUID idProducto, @RequestParam UUID idProveedor) {
        return ResponseEntity.ok(service.getPrecio(idProducto, idProveedor));
    }

    @PatchMapping
    ResponseEntity<PrecioResponseDTO> updatePrecio(@Valid @RequestBody PrecioRequestDTO productoRequestDTO) {
        return ResponseEntity.ok(service.setPrecio(productoRequestDTO));
    }

    @GetMapping("/listar")
    ResponseEntity<List<PrecioResponseDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }
}