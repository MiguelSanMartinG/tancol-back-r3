package com.main.tancol.liquidacion.controller;

import com.main.tancol.common.dto.PageResponse;
import com.main.tancol.liquidacion.dto.request.CancelacionLiquidacionRequestDTO;
import com.main.tancol.liquidacion.dto.response.LiquidacionListResponseDTO;
import com.main.tancol.liquidacion.dto.request.CancelacionPartidaRequestDTO;
import com.main.tancol.liquidacion.dto.request.LiquidacionRequestDTO;
import com.main.tancol.liquidacion.dto.response.LiquidacionResponseDTO;
import com.main.tancol.liquidacion.dto.response.NotaDTO;
import com.main.tancol.liquidacion.service.LiquidacionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/liquidacion")
@Tag(name = "💰 Liquidaciones")
public class LiquidacionController {

    private final LiquidacionService service;


    public LiquidacionController(LiquidacionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<LiquidacionResponseDTO> create(@Valid @RequestBody LiquidacionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<LiquidacionResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<LiquidacionResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Lista paginada para la tabla. Filtros (filter_*): fecha, folio, bloqueNota, estatus, partidas, totalEstimado,
     * createdAt, updatedAt, ruta, rutaIds, chofer, choferIds, ayudante, ayudanteIds, vehiculo, vehiculoIds.
     * Orden (sort_*=ASC|DESC): fecha, folio, createdAt, updatedAt, estatus, bloqueNota, ruta, chofer, partidas,
     * totalEstimado. Por defecto fecha DESC, createdAt DESC.
     */
    @GetMapping("/page")
    public ResponseEntity<PageResponse<LiquidacionListResponseDTO>> page(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(service.getPage(params));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LiquidacionResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LiquidacionResponseDTO> update(@PathVariable UUID id, @RequestBody LiquidacionRequestDTO dto) {
        return ResponseEntity.ok(service.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@Valid @PathVariable UUID id) {
        try{
            service.delete(id);
            Map<String, String> response = new HashMap<>();
            response.put("status", "Borrado");
            response.put("status", "exito");
            return ResponseEntity.ok(response);
        }catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("nota/{id}")
    public ResponseEntity<NotaDTO> obtenerNotaMasAlta(@PathVariable String id) {
        return ResponseEntity.ok(service.notaMasAlta(id));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<LiquidacionResponseDTO> cancelarLiquidacion(
            @PathVariable UUID id,
            @Valid @RequestBody CancelacionLiquidacionRequestDTO dto) {
        return ResponseEntity.ok(service.cancelarLiquidacion(id, dto));
    }

    @PostMapping("/{liquidacionId}/partidas/{partidaId}/cancelar")
    public ResponseEntity<LiquidacionResponseDTO> cancelarPartida(
            @PathVariable UUID liquidacionId,
            @PathVariable UUID partidaId,
            @Valid @RequestBody CancelacionPartidaRequestDTO dto) {
        return ResponseEntity.ok(service.cancelarPartida(liquidacionId, partidaId, dto));
    }

}
