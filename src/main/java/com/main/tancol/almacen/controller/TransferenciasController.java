package com.main.tancol.almacen.controller;

import com.main.tancol.almacen.dto.request.TransferenciasRequestDTO;
import com.main.tancol.almacen.dto.response.partidas.PartidaTransferenciaResponseDTO;
import com.main.tancol.almacen.dto.response.TransferenciasResponseDTO;
import com.main.tancol.almacen.service.PartidaTransferenciaService;
import com.main.tancol.almacen.service.TransferenciasService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@RestController
@RequestMapping("/transferencias")
@Tag(name = "➡️ Transferencias")
public class TransferenciasController {

    private final TransferenciasService service;
    private final PartidaTransferenciaService partidaService;


    public TransferenciasController(TransferenciasService service, PartidaTransferenciaService partidaService) {
        this.service = service;
        this.partidaService = partidaService;
    }

    @PostMapping
    public ResponseEntity<TransferenciasResponseDTO> create(@Valid @RequestBody TransferenciasRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<TransferenciasResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<TransferenciasResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransferenciasResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    @Hidden
    public ResponseEntity<TransferenciasResponseDTO> update(@PathVariable UUID id, @RequestBody TransferenciasRequestDTO dto) {
        return ResponseEntity.ok(service.update(id,dto));
    }

    @DeleteMapping("/{id}")
    @Hidden
    public ResponseEntity<TransferenciasResponseDTO> delete(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }

}
