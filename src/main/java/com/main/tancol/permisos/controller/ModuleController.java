package com.main.tancol.permisos.controller;

import com.main.tancol.permisos.dto.request.ModuleRequestDTO;
import com.main.tancol.permisos.dto.response.ModuleResponseDTO;
import com.main.tancol.permisos.service.ModuleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/modulo")
@Tag(name = "🦠 Modulos")
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleService moduleService;


    @PostMapping
    public ResponseEntity<ModuleResponseDTO> create(@Valid @RequestBody ModuleRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.moduleService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<ModuleResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<ModuleResponseDTO> response = this.moduleService.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModuleResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(moduleService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ModuleResponseDTO> update(@PathVariable UUID id, @RequestBody ModuleRequestDTO dto) {
        return ResponseEntity.ok(moduleService.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ModuleResponseDTO> delete(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(moduleService.delete(id));
    }

}
