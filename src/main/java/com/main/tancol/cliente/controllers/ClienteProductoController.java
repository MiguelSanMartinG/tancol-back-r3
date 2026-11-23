package com.main.tancol.cliente.controllers;

import com.main.tancol.cliente.dto.request.ClienteProductoRequestDTO;
import com.main.tancol.cliente.dto.response.ClienteProductoResponseDTO;
import com.main.tancol.cliente.entities.ClienteProductoEntity;
import com.main.tancol.cliente.enums.TipoActualizacionPrecio;
import com.main.tancol.cliente.service.ClienteProductoService;
import com.main.tancol.cliente.util.ClienteProductoRelValueMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/precio-cliente")
@Tag(name = "\uD83D\uDCB5 Precio Cliente")
public class ClienteProductoController {

    private final ClienteProductoService service;


    public ClienteProductoController(ClienteProductoService service) {
        this.service = service;
    }


    @GetMapping
    public ResponseEntity<List<ClienteProductoResponseDTO>> findAll(@RequestParam Map<String, String> params) {
        List<ClienteProductoResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Set<ClienteProductoResponseDTO>> update(@PathVariable UUID id, @RequestBody Set<ClienteProductoRequestDTO> dto,
                                                                  @RequestParam(required = false) String tipo,
                                                                  @RequestParam(required = false) UUID operacion) {
        // tipo=MASIVA y un mismo id de operación los manda la asignación masiva (pestaña Precios) para cada cliente.
        TipoActualizacionPrecio tipoActualizacion = "MASIVA".equalsIgnoreCase(tipo)
                ? TipoActualizacionPrecio.MASIVA
                : TipoActualizacionPrecio.INDIVIDUAL;
        return ResponseEntity.ok(service.update(id, dto, tipoActualizacion, operacion));
    }

}
