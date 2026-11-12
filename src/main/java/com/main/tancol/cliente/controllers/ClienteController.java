package com.main.tancol.cliente.controllers;

import com.main.tancol.cliente.dto.request.ClienteRequestDTO;
import com.main.tancol.cliente.dto.response.ClienteNombreHistorialResponseDTO;
import com.main.tancol.cliente.dto.response.ClienteResponseDTO;
import com.main.tancol.cliente.dto.response.HistoricoPrecioClienteResponseDTO;
import com.main.tancol.cliente.service.ClienteProductoService;
import com.main.tancol.cliente.service.ClienteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/cliente")
@Tag(name = "👩🏾‍💼 Cliente")
public class ClienteController {

    private final ClienteService service;
    private final ClienteProductoService clienteProductoService;

    public ClienteController(ClienteService service, ClienteProductoService clienteProductoService) {
        this.service = service;
        this.clienteProductoService = clienteProductoService;
    }

    @PostMapping
//    @PreAuthorize("hasAuthority('CLIENTE_WRITE')")
    public ResponseEntity<ClienteResponseDTO> create(@Valid @RequestBody ClienteRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.create(dto));
    }

    @GetMapping
//    @PreAuthorize("hasAuthority('CLIENTE_READ')")
    public ResponseEntity<List<ClienteResponseDTO>> findAll(@RequestParam Map<String, String> params) {

         List<ClienteResponseDTO> response = this.service.getAll(params);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasAuthority('CLIENTE_READ')")
    public ResponseEntity<ClienteResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/{id}/historial/nombre-comercial")
//    @PreAuthorize("hasAuthority('CLIENTE_READ')")
    public ResponseEntity<List<ClienteNombreHistorialResponseDTO>> getHistorialNombreComercial(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getHistorialNombreComercial(id));
    }

    @GetMapping("/{id}/historial/razon-social")
//    @PreAuthorize("hasAuthority('CLIENTE_READ')")
    public ResponseEntity<List<ClienteNombreHistorialResponseDTO>> getHistorialRazonSocial(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getHistorialRazonSocial(id));
    }

    @GetMapping("/{id}/historial/precios")
//    @PreAuthorize("hasAuthority('CLIENTE_READ')")
    public ResponseEntity<List<HistoricoPrecioClienteResponseDTO>> getHistorialPrecios(@PathVariable UUID id) {
        return ResponseEntity.ok(clienteProductoService.getHistorialPrecios(id));
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasAuthority('CLIENTE_WRITE')")
    public ResponseEntity<ClienteResponseDTO> update(@PathVariable UUID id, @RequestBody ClienteRequestDTO dto) {
        return ResponseEntity.ok(service.update(id,dto));
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAuthority('CLIENTE_DELETE')")
    public ResponseEntity<ClienteResponseDTO> delete(@Valid @PathVariable UUID id) {
        return ResponseEntity.ok(service.delete(id));
    }

}
