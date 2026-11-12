package com.main.tancol.orden.dto.request;

import com.main.tancol.producto.dto.request.PrecioRequestDTO;
import lombok.Value;

import java.util.UUID;

@Value
public class RecepcionPartidaOrdenRequestDTO {
    // SOLAMENTE EN RECIBO
     UUID idPartida;
     UUID idAlmacen;
     Integer unidadesRecibidas;
//     String comentario;
     PrecioRequestDTO presentacionProducto;
     boolean verificado;
}
