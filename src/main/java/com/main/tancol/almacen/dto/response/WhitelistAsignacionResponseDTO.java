package com.main.tancol.almacen.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class WhitelistAsignacionResponseDTO {
    private UUID idEmpleado;
    private String nombreEmpleado;
    private UUID idDepartamento;
    private String nombreDepartamento;
    private int empleados;
    private int agregados;
    private int yaExistian;
    private int retirados;
    private List<WhitelistProductoResponseDTO> asignaciones;
}
