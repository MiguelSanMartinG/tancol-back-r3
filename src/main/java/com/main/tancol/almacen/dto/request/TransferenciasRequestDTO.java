package com.main.tancol.almacen.dto.request;

import com.main.tancol.almacen.dto.request.partidas.PartidaTransferenciaRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Value
public class TransferenciasRequestDTO {
    // Agrega campos aqu??
    @NotEmpty(message = "Debe incluir al menos una partida")
    @Valid
    List<PartidaTransferenciaRequestDTO> transferencias;
    String motivo;
    String comentario;
    @NotNull
    UUID idUsuario;
    LocalDateTime fecha;

//    List<TransferenciaPartidaRequestDTO> partidas;
}
