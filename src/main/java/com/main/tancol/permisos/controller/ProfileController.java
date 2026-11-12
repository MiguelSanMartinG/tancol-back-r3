package com.main.tancol.permisos.controller;

import com.main.tancol.permisos.dto.request.ProfileRequestDTO;
import com.main.tancol.permisos.dto.response.ProfileResponseDTO;
import com.main.tancol.permisos.service.ProfileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@RestController
@RequestMapping("/Profile")
@Tag(name = "📰 Profil")
public class ProfileController {

    private final ProfileService service;


    public ProfileController(ProfileService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ProfileResponseDTO> create(@Valid @RequestBody ProfileRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<ProfileResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<ProfileResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfileResponseDTO> update(@PathVariable UUID id, @RequestBody ProfileRequestDTO dto) {
        return ResponseEntity.ok(service.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProfileResponseDTO> delete(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }

}
