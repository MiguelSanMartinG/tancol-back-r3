package com.main.tancol.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopClientePgExpResponseDTO {
    private UUID idCliente;
    private String cliente;
    private BigDecimal unidadesTotales;
    private BigDecimal unidadesPg;
    private BigDecimal unidadesExp;
    private BigDecimal importeTotal;
    private LocalDate ultimaFechaConsumo;
}
