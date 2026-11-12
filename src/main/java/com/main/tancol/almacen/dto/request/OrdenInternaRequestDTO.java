package com.main.tancol.almacen.dto.request;

import com.main.tancol.almacen.dto.request.partidas.PartidaOrdenInternaRequestDTO;
import com.main.tancol.almacen.enums.EstadoOrdenInterna;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Value
public class OrdenInternaRequestDTO {
    @NotBlank
    String uso;

    @NotNull(message = "El empleado emisor es requerido")
    UUID empleadoEmisor;

    @NotNull(message = "El empleado receptor es requerido")
    UUID empleadoReceptor;
    LocalDateTime fechaElaboracion;
    String comentarios;
    EstadoOrdenInterna estado;
    @NotEmpty(message = "Debe incluir al menos una partida")
    @Valid
    List<PartidaOrdenInternaRequestDTO> partidas;
}
