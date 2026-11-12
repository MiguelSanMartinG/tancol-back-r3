package com.main.tancol.orden.dto.request;

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
    private final UUID idOrden;
    private final String comentarioRecibo;
    private final List<PartidaOrdenEntityDTO> partidas;

    /**
     * DTO for {@link com.main.tancol.orden.entities.PartidaOrdenEntity}
     */
    @AllArgsConstructor
    @Getter
    public static class PartidaOrdenEntityDTO implements Serializable {
        private final Integer unidadesRecibidas;
        private final String comentario;
        private final UUID almacenId;
    }
}