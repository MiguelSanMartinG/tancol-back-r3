package com.main.tancol.logistica.dto.request;

import com.main.tancol.logistica.enums.TipoRuta;
import lombok.Value;

import java.util.Set;
import java.util.UUID;

@Value
public class RutasRequestDTO {
     String nombre;
     String numero;
     UUID idProductoC;
     UUID idAyudante;
     UUID idChofer;
     UUID idVehiculo;
     String letraPredeterminada;
     TipoRuta tipoRuta;
     Set<UUID> empleadosAsignados;
     boolean enabled;
}
