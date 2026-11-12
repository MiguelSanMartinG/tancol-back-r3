package com.main.tancol.prestamo.dto.response;

import com.main.tancol.cliente.dto.response.ClienteResponseBasicoDTO;
import com.main.tancol.prestamo.enums.EstadoEquipo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class EquipoResponseDTO {
    private UUID id;
    private String nombre;
    private String sku;
    private String idImagen;
    private String descripcion;
    private String marca;
    private String modelo;
    private EstadoEquipo estado;
    private TipoEquipoSimpleResponseDTO tipoEquipo;
    private ClienteResponseBasicoDTO clienteAsignado;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean enabled;
}
