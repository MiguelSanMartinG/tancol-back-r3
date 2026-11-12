package com.main.tancol.permisos.controller;

import com.main.tancol.permisos.dto.request.RoleRequestDTO;
import com.main.tancol.permisos.dto.response.RoleResponseDTO;
import com.main.tancol.permisos.enums.ModuleEnum;
import com.main.tancol.permisos.enums.PermissionEnum;
import com.main.tancol.permisos.service.RoleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rol")
@Tag(name = "📇 Roles")
public class RoleController {

    private final RoleService service;

    public RoleController(RoleService service) {
        this.service = service;
    }

    @PostMapping
//    @PreAuthorize("hasAuthority('ROL:WRITE')")
    public ResponseEntity<RoleResponseDTO> create(@Valid @RequestBody RoleRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto));
    }

    @GetMapping
//    @PreAuthorize("hasAuthority('ROL:READ')")
    public ResponseEntity<List<RoleResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<RoleResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasAuthority('ROL:READ')")
    public ResponseEntity<RoleResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasAuthority('ROL:WRITE')")
    public ResponseEntity<RoleResponseDTO> update(@PathVariable Long id, @RequestBody RoleRequestDTO dto) {
        return ResponseEntity.ok(service.update(id,dto));
    }

//    @PutMapping("/permisos/{id}")
////    @PreAuthorize("hasAuthority('ROL:WRITE')")
//    public ResponseEntity<?> updatePermisos(@PathVariable Long id, @RequestBody RoleRequestDTO dto) {
//        return ResponseEntity.ok(service.updatePermisos(id,dto));
//    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAuthority('ROL:DELETE')")
    public ResponseEntity<RoleResponseDTO> delete(@Valid @PathVariable Long id) {
        return ResponseEntity.ok(service.delete(id));
    }

//    @GetMapping("/modulos")
////    @PreAuthorize("hasAuthority('ROL:READ')")
//    public List<ModuleEnum> getModules() {
//        return Arrays.stream(ModuleEnum.values()).toList();
//    }
//
//    @GetMapping("/acciones")
////    @PreAuthorize("hasAuthority('ROL:READ')")
//    public List<PermissionEnum> getPermissions() {
//        return Arrays.stream(PermissionEnum.values()).toList();
//    }



}
