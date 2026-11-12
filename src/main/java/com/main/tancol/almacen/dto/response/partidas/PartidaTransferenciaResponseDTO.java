package com.main.tancol.almacen.dto.response.partidas;

import com.main.tancol.almacen.dto.response.UnidadesMedidaResponseDTO;
import com.main.tancol.producto.dto.response.insumos.ProductoBasicoSimpleResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class PartidaTransferenciaResponseDTO {
    private UUID idPartidaTransferencia;
    private String nombreUsuario;
    private LocalDateTime fechaDeTransferencia;
    private UUID idAlmacenOrigen;
    private String nombreAlmacenOrigen;
    private UUID idAlmacenDestino;
    private String nombreAlmacenDestino;
    private BigDecimal cantidadTrasferida;
    private UnidadesMedidaResponseDTO unidadesMedida;
    private ProductoBasicoSimpleResponseDTO producto;
    private String comentario;
    private LocalDateTime createdAt;



    // Agrega m??s campos aqu??
}
