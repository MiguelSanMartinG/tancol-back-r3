package com.main.tancol.medidores.controller;

import com.main.tancol.common.dto.PageResponse;
import com.main.tancol.medidores.dto.request.MedidoresRelacionRequestDTO;
import com.main.tancol.medidores.dto.request.MedidoresRequestDTO;
import com.main.tancol.medidores.dto.request.ResetMedidorRequestDTO;
import com.main.tancol.medidores.dto.response.MedidoresResponseDTO;
import com.main.tancol.medidores.service.MedidoresService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/medidores")
@Tag(name = "🎚️ Medidores")
public class MedidoresController {

    private final MedidoresService service;


    public MedidoresController(MedidoresService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<MedidoresResponseDTO> create(@Valid @RequestBody MedidoresRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto));
    }

    @PostMapping("/reset")
    public ResponseEntity<Void> reset(@Valid @RequestBody ResetMedidorRequestDTO dto) {
        this.service.resetMedidor(dto.getIdMedidor(), dto.getMensaje());
        return ResponseEntity.noContent().build(); // 204
    }

    @GetMapping
    public ResponseEntity<List<MedidoresResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<MedidoresResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/page")
    public ResponseEntity<PageResponse<MedidoresResponseDTO>> page(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(service.getPage(params));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedidoresResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedidoresResponseDTO> update(@PathVariable UUID id, @RequestBody MedidoresRequestDTO dto) {
        return ResponseEntity.ok(service.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MedidoresResponseDTO> delete(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }

    @PostMapping("/crear-relaccion")
    public ResponseEntity<Map<String, String>> crearHijo(@Valid @RequestBody MedidoresRelacionRequestDTO dto) {
        return this.service.agregarHijo(dto.getIdMedidorPadre(),dto.getIdMedidorHijo());
    }

    @PostMapping("/borrar-relaccion")
    public ResponseEntity<Map<String, String>> borrarHijo(@Valid @RequestBody MedidoresRelacionRequestDTO dto) {
        return this.service.borrarHijo(dto.getIdMedidorPadre(),dto.getIdMedidorHijo());
    }


}
