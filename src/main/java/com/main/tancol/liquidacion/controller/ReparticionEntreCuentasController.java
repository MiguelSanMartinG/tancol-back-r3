package com.main.tancol.liquidacion.controller;

import com.main.tancol.liquidacion.dto.request.ReparticionEntreCuentasRequestDTO;
import com.main.tancol.liquidacion.dto.response.ReparticionEntreCuentasResponseDTO;
import com.main.tancol.liquidacion.entities.ReparticionEntreCuentasEntity;
import com.main.tancol.liquidacion.service.ReparticionEntreCuentasService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tabla-descuentos")
@Tag(name = "👛 Tabla Descuentos")
public class ReparticionEntreCuentasController {

    private final ReparticionEntreCuentasService service;


    public ReparticionEntreCuentasController(ReparticionEntreCuentasService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ReparticionEntreCuentasResponseDTO> create(@Valid @RequestBody ReparticionEntreCuentasRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<ReparticionEntreCuentasResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<ReparticionEntreCuentasResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/latest")
    public ResponseEntity<Map<String, Object>>getLatest(@RequestParam Map<String, String> params) {
        Map<String, Object> model = new HashMap<>();
        ReparticionEntreCuentasEntity tabla = this.service.getLatest();
        model.put("a1", tabla.getCuentaA());
        model.put("a2", tabla.getCuentaB());
        model.put("a3", tabla.getCuentaC());
        model.put("total", tabla.getCuentaA().add(tabla.getCuentaB()).add(tabla.getCuentaC()));
//        Map<String, Object> model = new HashMap<>();
        return new ResponseEntity<>(model, HttpStatus.OK);
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<ReparticionEntreCuentasResponseDTO> getById(@PathVariable UUID id) {
//        return ResponseEntity.ok(service.getById(id));
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<ReparticionEntreCuentasResponseDTO> update(@PathVariable UUID id, @RequestBody ReparticionEntreCuentasRequestDTO dto) {
//        return ResponseEntity.ok(service.update(id,dto));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<ReparticionEntreCuentasResponseDTO> delete(@Valid @PathVariable UUID id) {
//        return ResponseEntity.ok(service.delete(id));
//    }

}
