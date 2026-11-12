package com.main.tancol.logistica.dto.response;

import com.main.tancol.empleados.dto.response.EmpleadosBasicResponseDTO;
import com.main.tancol.logistica.enums.TipoRuta;
import com.main.tancol.producto.dto.response.comercial.ProductoComercialResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class RutasResponseDTO {
    private UUID id;
    private String nombre;
    private String numero;
    private TipoRuta tipoRuta;
    private String notaAsignada;
    private EmpleadosBasicResponseDTO choferBase;
    private ProductoComercialResponseDTO productoBase;
    private VehiculosResponseDTO vehiculosBase;
    private Set<EmpleadosBasicResponseDTO> empleadosAsignados;
    private Boolean enabled;
    private LocalDateTime cratedAt;
    private LocalDateTime updatedAt;
}
