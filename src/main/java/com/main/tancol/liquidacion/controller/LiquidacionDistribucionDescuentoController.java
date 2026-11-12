package com.main.tancol.liquidacion.controller;

import com.main.tancol.liquidacion.dto.request.LiquidacionDistribucionDescuentoRequestDTO;
import com.main.tancol.liquidacion.dto.response.LiquidacionDistribucionDescuentoResponseDTO;
import com.main.tancol.liquidacion.service.LiquidacionDistribucionDescuentoService;
import com.main.tancol.liquidacion.service.ReparticionEntreCuentasService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/dinero-desviado")
@Tag(name = "🍜 Caja China")
public class LiquidacionDistribucionDescuentoController {

    private final LiquidacionDistribucionDescuentoService service;
    private final ReparticionEntreCuentasService reparticionEntreCuentasService;


    public LiquidacionDistribucionDescuentoController(LiquidacionDistribucionDescuentoService service, ReparticionEntreCuentasService reparticionEntreCuentasService) {
        this.service = service;
        this.reparticionEntreCuentasService = reparticionEntreCuentasService;
    }

//    @PostMapping
    public ResponseEntity<LiquidacionDistribucionDescuentoResponseDTO> create(@Valid @RequestBody LiquidacionDistribucionDescuentoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<LiquidacionDistribucionDescuentoResponseDTO>> findAll(@RequestParam Map<String, String> params) {
//        ReparticionEntreCuentasEntity d = reparticionEntreCuentasService.getLatest();
//        log.info(d.getCreatedAt().toString());
        List<LiquidacionDistribucionDescuentoResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
//        return  null;
    }

//    @GetMapping("/{id}")
    public ResponseEntity<LiquidacionDistribucionDescuentoResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

//    @PutMapping("/{id}")
    public ResponseEntity<LiquidacionDistribucionDescuentoResponseDTO> update(@PathVariable UUID id, @RequestBody LiquidacionDistribucionDescuentoRequestDTO dto) {
        return ResponseEntity.ok(service.update(id,dto));
    }

//    @DeleteMapping("/{id}")
    public ResponseEntity<LiquidacionDistribucionDescuentoResponseDTO> delete(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }

}
