package com.main.tancol.cuadreinventario.controllers;

import com.main.tancol.common.dto.PageResponse;
import com.main.tancol.cuadreinventario.dto.CuadreInventarioDTOs.CapturaRequest;
import com.main.tancol.cuadreinventario.dto.CuadreInventarioDTOs.CerrarRequest;
import com.main.tancol.cuadreinventario.dto.CuadreInventarioDTOs.CrearRequest;
import com.main.tancol.cuadreinventario.dto.CuadreInventarioDTOs.Response;
import com.main.tancol.cuadreinventario.enums.EstadoCuadre;
import com.main.tancol.cuadreinventario.service.CuadreInventarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/cuadre-inventario")
@Tag(name = "Cuadre de inventario")
public class CuadreInventarioController {

    private final CuadreInventarioService service;

    public CuadreInventarioController(CuadreInventarioService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Response>> listar(@RequestParam(required = false) UUID idAlmacen,
                                                 @RequestParam(required = false) EstadoCuadre estado) {
        return ResponseEntity.ok(service.listar(idAlmacen, estado));
    }

    @GetMapping("/page")
    public ResponseEntity<PageResponse<Response>> page(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(service.getPage(params));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> detalle(@PathVariable UUID id) {
        return ResponseEntity.ok(service.detalle(id));
    }

    @PostMapping
    public ResponseEntity<Response> crear(@Valid @RequestBody CrearRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PutMapping("/{id}/partidas")
    public ResponseEntity<Response> capturar(@PathVariable UUID id, @Valid @RequestBody CapturaRequest dto) {
        return ResponseEntity.ok(service.capturar(id, dto));
    }

    @PostMapping("/{id}/cerrar")
    public ResponseEntity<Response> cerrar(@PathVariable UUID id, @Valid @RequestBody CerrarRequest dto) {
        return ResponseEntity.ok(service.cerrar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> cancelar(@PathVariable UUID id) {
        return ResponseEntity.ok(service.cancelar(id));
    }
}
