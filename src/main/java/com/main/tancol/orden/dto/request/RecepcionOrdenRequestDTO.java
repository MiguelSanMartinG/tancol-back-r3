package com.main.tancol.orden.dto.request;

import com.main.tancol.orden.enums.RecepcionEstatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * DTO for {@link com.main.tancol.orden.entities.OrdenEntity}
 */
@AllArgsConstructor
@Getter
public class RecepcionOrdenRequestDTO implements Serializable {
    private UUID idOrden;
    private String comentarioRecibo;
    private List<RecepcionPartidaOrdenRequestDTO> partidas;
    private RecepcionEstatus recepcionEstatus;

}