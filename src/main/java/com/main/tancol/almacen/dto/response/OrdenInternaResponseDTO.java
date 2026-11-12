package com.main.tancol.almacen.dto.response;

import com.main.tancol.almacen.dto.response.partidas.PartidaOrdenInternaResponseDTO;
import com.main.tancol.almacen.dto.response.partidas.PartidasLiquidacionResponseDTO;
import com.main.tancol.almacen.enums.EstadoOrdenInterna;
import com.main.tancol.empleados.dto.response.EmpleadosResponseDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class OrdenInternaResponseDTO {
    private UUID idOrdenInterna;
    private String uso;
    private String folio;
    private EmpleadosResponseDTO empleadoEmisor;
    private EmpleadosResponseDTO empleadoReceptor;
    private String comentario;
    private LocalDateTime fechaElaboracion;
    private EstadoOrdenInterna estado;
    private List<PartidaOrdenInternaResponseDTO> partidas;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
