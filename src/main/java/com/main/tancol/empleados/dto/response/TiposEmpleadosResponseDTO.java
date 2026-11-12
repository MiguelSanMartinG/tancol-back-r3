package com.main.tancol.empleados.dto.response;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.grammars.hql.HqlParser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@SuperBuilder
public class TiposEmpleadosResponseDTO {
    private UUID idTipoEmpleado;
    private String nombre;
    private String alias;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean enabled;
    List<EmpleadosResponseDTO> empleados;
}
