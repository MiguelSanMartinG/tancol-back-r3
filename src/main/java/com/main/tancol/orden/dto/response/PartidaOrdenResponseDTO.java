package com.main.tancol.orden.dto.response;

import com.main.tancol.almacen.dto.response.AlmacenResponseDTO;
import com.main.tancol.almacen.entities.Almacen;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class PartidaOrdenResponseDTO {
    private UUID id;
    private Integer cantidad;
    private BigDecimal precio;
    private Integer unidadesRecibidas;
    private String comentariosRecibo;
    private AlmacenResponseDTO almacen;
    // Agrega m??s campos aqu??
}
