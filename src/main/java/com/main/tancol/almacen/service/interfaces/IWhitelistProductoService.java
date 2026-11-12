package com.main.tancol.almacen.service.interfaces;

import com.main.tancol.almacen.dto.request.WhitelistAsignacionDepartamentoRequestDTO;
import com.main.tancol.almacen.dto.request.WhitelistAsignacionMasivaRequestDTO;
import com.main.tancol.almacen.dto.request.WhitelistConfiguracionRequestDTO;
import com.main.tancol.almacen.dto.request.WhitelistProductoRequestDTO;
import com.main.tancol.almacen.dto.request.WhitelistProductoUpdateRequestDTO;
import com.main.tancol.almacen.dto.response.ProductosPermitidosResponseDTO;
import com.main.tancol.almacen.dto.response.WhitelistAsignacionResponseDTO;
import com.main.tancol.almacen.dto.response.WhitelistConfiguracionResponseDTO;
import com.main.tancol.almacen.dto.response.WhitelistProductoResponseDTO;
import com.main.tancol.empleados.entities.EmpleadosEntity;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IWhitelistProductoService {

    WhitelistProductoResponseDTO create(WhitelistProductoRequestDTO dto);

    List<WhitelistProductoResponseDTO> getAll(Map<String, String> params);

    WhitelistProductoResponseDTO getById(UUID id);

    WhitelistProductoResponseDTO update(UUID id, WhitelistProductoUpdateRequestDTO dto);

    WhitelistProductoResponseDTO delete(UUID id);

    WhitelistAsignacionResponseDTO asignarMasivo(WhitelistAsignacionMasivaRequestDTO dto);

    WhitelistAsignacionResponseDTO asignarDepartamento(WhitelistAsignacionDepartamentoRequestDTO dto);

    ProductosPermitidosResponseDTO productosPermitidos(UUID idEmpleado);

    WhitelistConfiguracionResponseDTO configuracion();

    WhitelistConfiguracionResponseDTO actualizarConfiguracion(WhitelistConfiguracionRequestDTO dto);

    void validarSolicitud(EmpleadosEntity empleado, Collection<UUID> productoIds);
}
