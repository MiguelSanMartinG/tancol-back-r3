package com.main.tancol.almacen.dto.response;

import com.main.tancol.empleados.dto.response.DepartamentoSimpleResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class WhitelistProductoResponseDTO {
    private UUID id;
    private UUID idEmpleado;
    private String nombreEmpleado;
    private DepartamentoSimpleResponseDTO departamento;
    private UUID idProducto;
    private String nombreProducto;
    private String sku;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
