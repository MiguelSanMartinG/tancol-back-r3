package com.main.tancol.logistica.dto.request;

import com.main.tancol.logistica.dto.request.partidas.BitacoraPartidaVentasRequestDTO;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Value
public class BitacoraVentaRequestDTO {
    LocalDate fecha;
}
