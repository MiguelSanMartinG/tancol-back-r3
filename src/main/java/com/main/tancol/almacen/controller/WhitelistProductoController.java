package com.main.tancol.almacen.controller;

import com.main.tancol.almacen.dto.request.WhitelistAsignacionDepartamentoRequestDTO;
import com.main.tancol.almacen.dto.request.WhitelistAsignacionMasivaRequestDTO;
import com.main.tancol.almacen.dto.request.WhitelistConfiguracionRequestDTO;
import com.main.tancol.almacen.dto.request.WhitelistProductoRequestDTO;
import com.main.tancol.almacen.dto.request.WhitelistProductoUpdateRequestDTO;
import com.main.tancol.almacen.dto.response.ProductosPermitidosResponseDTO;
import com.main.tancol.almacen.dto.response.WhitelistAsignacionResponseDTO;
import com.main.tancol.almacen.dto.response.WhitelistConfiguracionResponseDTO;
import com.main.tancol.almacen.dto.response.WhitelistProductoResponseDTO;
import com.main.tancol.almacen.service.WhitelistProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/whitelist-insumo")
@Tag(name = "Whitelist de insumos")
public class WhitelistProductoController {

    private final WhitelistProductoService service;

    public WhitelistProductoController(WhitelistProductoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<WhitelistProductoResponseDTO> create(@Valid @RequestBody WhitelistProductoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<WhitelistProductoResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(service.getAll(params));
    }

    @GetMapping("/configuracion")
    @Operation(summary = "Consulta si la whitelist de insumos está activa")
    public ResponseEntity<WhitelistConfiguracionResponseDTO> configuracion() {
        return ResponseEntity.ok(service.configuracion());
    }

    @PutMapping("/configuracion")
    @Operation(summary = "Activa o desactiva de forma global la whitelist de insumos")
    public ResponseEntity<WhitelistConfiguracionResponseDTO> actualizarConfiguracion(@Valid @RequestBody WhitelistConfiguracionRequestDTO dto) {
        return ResponseEntity.ok(service.actualizarConfiguracion(dto));
    }

    @GetMapping("/productos-permitidos")
    @Operation(summary = "Productos que el empleado puede solicitar. Sin idEmpleado usa la sesión actual")
    public ResponseEntity<ProductosPermitidosResponseDTO> productosPermitidos(@RequestParam(required = false) UUID idEmpleado) {
        return ResponseEntity.ok(service.productosPermitidos(idEmpleado));
    }

    @PostMapping("/asignacion-masiva")
    @Operation(summary = "Asigna varios productos a un empleado. Con reemplazar=true deja solo esos productos")
    public ResponseEntity<WhitelistAsignacionResponseDTO> asignarMasivo(@Valid @RequestBody WhitelistAsignacionMasivaRequestDTO dto) {
        return ResponseEntity.ok(service.asignarMasivo(dto));
    }

    @PostMapping("/asignacion-departamento")
    @Operation(summary = "Asigna los mismos productos a todos los empleados activos de un departamento")
    public ResponseEntity<WhitelistAsignacionResponseDTO> asignarDepartamento(@Valid @RequestBody WhitelistAsignacionDepartamentoRequestDTO dto) {
        return ResponseEntity.ok(service.asignarDepartamento(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WhitelistProductoResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WhitelistProductoResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody WhitelistProductoUpdateRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<WhitelistProductoResponseDTO> delete(@PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }
}
