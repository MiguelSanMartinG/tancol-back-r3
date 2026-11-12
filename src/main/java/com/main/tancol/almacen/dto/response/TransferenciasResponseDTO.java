package com.main.tancol.almacen.dto.response;

import com.main.tancol.almacen.dto.request.partidas.PartidaTransferenciaRequestDTO;
import com.main.tancol.almacen.dto.response.partidas.PartidaTransferenciaResponseDTO;
import com.main.tancol.empleados.dto.response.EmpleadosResponseDTO;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class TransferenciasResponseDTO {
    private UUID idTransferencia;
    private String motivo;
    private String comentario;
    private EmpleadosResponseDTO idEmpleado; //PersonaTransfiere
    private LocalDateTime fechaTransferencia;
    private List<PartidaTransferenciaResponseDTO> transferencias;
    // Agrega m??s campos aqu??
}
