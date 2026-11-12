package com.main.tancol.almacen.dto.response;

import com.main.tancol.empleados.dto.response.DepartamentoSimpleResponseDTO;
import com.main.tancol.producto.dto.response.insumos.ProductoBasicoSimpleResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class ProductosPermitidosResponseDTO {
    private boolean usarWhitelist;
    private UUID idEmpleado;
    private String nombreEmpleado;
    private DepartamentoSimpleResponseDTO departamento;
    private List<ProductoBasicoSimpleResponseDTO> productos;
}
