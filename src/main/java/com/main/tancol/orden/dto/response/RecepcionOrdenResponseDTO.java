package com.main.tancol.orden.dto.response;

import com.main.tancol.orden.enums.RecepcionEstatus;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.main.tancol.orden.entities.OrdenEntity}
 */
@SuperBuilder(toBuilder = true)
@Getter
@Setter
public class RecepcionOrdenResponseDTO extends FacturacionOrdenResponseDTO implements Serializable {

    private  LocalDateTime fechaRecibo;
    private RecepcionEstatus recepcionEstatus;
//    private String comentarioRecibo;
    private String usuario;
}