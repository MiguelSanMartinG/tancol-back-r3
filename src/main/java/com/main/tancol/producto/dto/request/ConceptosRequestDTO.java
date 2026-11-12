package com.main.tancol.producto.dto.request;

import com.main.tancol.producto.enums.TipoRezago;
import lombok.*;

@Value
public class ConceptosRequestDTO {
    // Agrega campos aqu??
    TipoRezago tipoRezago;
    String motivo;

}
