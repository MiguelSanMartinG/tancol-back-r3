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
public class MedidoresResponseDTO {
    private String nombre;
    private String alias;
    private UUID idMedidor;
    private TiposAgua tipoAgua;
    private BigDecimal ultimaMedicion;
    private LocalDateTime fechaUltimaMedicion;
    private List<SistemasDeAlmacenamientoResponseDTO> sistemasDeAlmacenamiento;
    private UnidadesMedidaResponseDTO unidadMedida;
    private List<MedidoresResponseSimpleDTO> medidoresHijos;
    private Boolean enabled;
    /** Existe bitácora registrada hoy para este medidor. */
    private Boolean bitacoraHoy;
    /** Alias de bitacoraHoy: indica si los datos del día están actualizados. */
    private Boolean medidoHoy;
    private BigDecimal consumoHoy;
    /** Solo medidores padre: suma del consumo de hoy de sus hijos. */
    private BigDecimal sumaConsumoHijos;
    /** Solo medidores padre: consumoHoy del padre menos sumaConsumoHijos. */
    private BigDecimal diferenciaVsHijos;
    /** Solo medidores padre: todos los hijos tienen bitácora hoy. */
    private Boolean todosHijosMedidosHoy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
}
