package com.main.tancol.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoEquipoCantidadResponseDTO {
    private UUID idTipoEquipo;
    private String tipoEquipo;
    private int cantidad;
}
