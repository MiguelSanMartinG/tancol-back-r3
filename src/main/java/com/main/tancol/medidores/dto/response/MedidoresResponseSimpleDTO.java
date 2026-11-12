package com.main.tancol.medidores.dto.response;

import com.main.tancol.almacen.dto.response.UnidadesMedidaResponseDTO;
import com.main.tancol.medidores.enums.TiposAgua;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class MedidoresResponseSimpleDTO {
    private String nombre;
    private String alias;
    private UUID idMedidor;
    private TiposAgua tipoAgua;
    private BigDecimal ultimaMedicion;
    private List<SistemasDeAlmacenamientoResponseDTO> sistemasDeAlmacenamiento;
    private UnidadesMedidaResponseDTO unidadMedida;
    private LocalDateTime fechaUltimaMedicion;
    private Boolean bitacoraHoy;
    private Boolean medidoHoy;
    private BigDecimal consumoHoy;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
}
