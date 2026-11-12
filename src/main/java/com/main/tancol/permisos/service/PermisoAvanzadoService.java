package com.main.tancol.permisos.service;

import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.empleados.helpers.EmpleadoContext;
import com.main.tancol.empleados.repositories.EmpleadosRepository;
import com.main.tancol.permisos.dto.request.AsignacionPermisoAvanzadoRequestDTO;
import com.main.tancol.permisos.dto.request.PermisoAvanzadoRequestDTO;
import com.main.tancol.permisos.dto.response.AsignacionPermisoAvanzadoResponseDTO;
import com.main.tancol.permisos.dto.response.PermisoAvanzadoResponseDTO;
import com.main.tancol.permisos.dto.response.TienePermisoAvanzadoResponseDTO;
import com.main.tancol.permisos.entities.EmpleadoPermisoAvanzadoEntity;
import com.main.tancol.permisos.entities.PermisoAvanzadoEntity;
import com.main.tancol.permisos.repositories.EmpleadoPermisoAvanzadoRepository;
import com.main.tancol.permisos.repositories.PermisoAvanzadoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermisoAvanzadoService {

    private final PermisoAvanzadoRepository permisoRepository;
    private final EmpleadoPermisoAvanzadoRepository asignacionRepository;
    private final EmpleadosRepository empleadosRepository;

    public List<PermisoAvanzadoResponseDTO> listarCatalogo() {
        return permisoRepository.findAllByDeletedFalse().stream()
                .map(this::toPermisoResponse)
                .toList();
    }

    @Transactional
    public PermisoAvanzadoResponseDTO crearPermiso(PermisoAvanzadoRequestDTO dto) {
        assertEsAdmin();
        String codigo = dto.getCodigo().trim().toUpperCase();
        if (permisoRepository.existsByCodigoAndDeletedFalse(codigo)) {
            throw new IllegalArgumentException("Ya existe un permiso avanzado con código: " + codigo);
        }
        PermisoAvanzadoEntity entity = PermisoAvanzadoEntity.builder()
                .codigo(codigo)
                .nombre(dto.getNombre().trim())
                .descripcion(StringUtils.trimToNull(dto.getDescripcion()))
                .build();
        return toPermisoResponse(permisoRepository.save(entity));
    }

    public List<AsignacionPermisoAvanzadoResponseDTO> listarAsignaciones(UUID empleadoId) {
        assertEsAdmin();
        List<EmpleadoPermisoAvanzadoEntity> list = empleadoId == null
                ? asignacionRepository.findAllByDeletedFalse()
                : asignacionRepository.findAllByEmpleado_IdAndDeletedFalse(empleadoId);
        return list.stream().map(this::toAsignacionResponse).toList();
    }

    @Transactional
    public AsignacionPermisoAvanzadoResponseDTO asignar(AsignacionPermisoAvanzadoRequestDTO dto) {
        assertEsAdmin();
        EmpleadosEntity admin = EmpleadoContext.getEmpleadoActual();

        EmpleadosEntity empleado = empleadosRepository.findById(dto.getEmpleadoId())
                .orElseThrow(() -> new IDNotFound("Empleado no encontrado"));

        PermisoAvanzadoEntity permiso = resolverPermiso(dto);

        if (asignacionRepository.existsByEmpleado_IdAndPermiso_CodigoAndDeletedFalse(
                empleado.getId(), permiso.getCodigo())) {
            throw new IllegalArgumentException(
                    "El empleado ya tiene asignado el permiso: " + permiso.getCodigo());
        }

        EmpleadoPermisoAvanzadoEntity asignacion = EmpleadoPermisoAvanzadoEntity.builder()
                .empleado(empleado)
                .permiso(permiso)
                .asignadoPor(admin.getNombreUsuario())
                .build();

        return toAsignacionResponse(asignacionRepository.save(asignacion));
    }

    @Transactional
    public void quitarAsignacion(UUID asignacionId) {
        assertEsAdmin();
        EmpleadoPermisoAvanzadoEntity asignacion = asignacionRepository.findByIdAndDeletedFalse(asignacionId)
                .orElseThrow(() -> new IDNotFound("Asignación no encontrada"));
        asignacion.setDeleted(true);
        asignacion.setEnabled(false);
        asignacionRepository.save(asignacion);
    }

    public List<PermisoAvanzadoResponseDTO> misPermisos() {
        EmpleadosEntity empleado = requireEmpleadoAutenticado();
        if (isAdmin(empleado)) {
            return listarCatalogo();
        }
        return asignacionRepository.findAllByEmpleado_IdAndDeletedFalse(empleado.getId()).stream()
                .map(a -> toPermisoResponse(a.getPermiso()))
                .toList();
    }

    public TienePermisoAvanzadoResponseDTO tengoPermiso(String codigo) {
        EmpleadosEntity empleado = requireEmpleadoAutenticado();
        boolean tiene = hasAdvancedPermission(empleado, codigo);
        return TienePermisoAvanzadoResponseDTO.builder()
                .codigo(codigo)
                .tienePermiso(tiene)
                .build();
    }

    public boolean hasAdvancedPermission(EmpleadosEntity user, String codigo) {
        if (user == null || StringUtils.isBlank(codigo)) {
            return false;
        }
        if (isAdmin(user)) {
            return true;
        }
        return asignacionRepository.existsByEmpleado_IdAndPermiso_CodigoAndDeletedFalse(
                user.getId(), codigo.trim().toUpperCase());
    }

    private PermisoAvanzadoEntity resolverPermiso(AsignacionPermisoAvanzadoRequestDTO dto) {
        if (dto.getPermisoId() != null) {
            return permisoRepository.findById(dto.getPermisoId())
                    .filter(p -> !p.isDeleted())
                    .orElseThrow(() -> new IDNotFound("Permiso avanzado no encontrado"));
        }
        if (StringUtils.isNotBlank(dto.getCodigo())) {
            return permisoRepository.findByCodigoAndDeletedFalse(dto.getCodigo().trim().toUpperCase())
                    .orElseThrow(() -> new IDNotFound("Permiso avanzado no encontrado: " + dto.getCodigo()));
        }
        throw new IllegalArgumentException("Debe indicar codigo o permisoId");
    }

    private void assertEsAdmin() {
        EmpleadosEntity emp = EmpleadoContext.getEmpleadoActual();
        if (!isAdmin(emp)) {
            throw new AccessDeniedException("Solo el usuario admin puede administrar permisos avanzados");
        }
    }

    private boolean isAdmin(EmpleadosEntity emp) {
        return emp != null && "admin".equalsIgnoreCase(emp.getNombreUsuario());
    }

    private EmpleadosEntity requireEmpleadoAutenticado() {
        EmpleadosEntity emp = EmpleadoContext.getEmpleadoActual();
        if (ObjectUtils.isEmpty(emp)) {
            throw new IllegalArgumentException("No se encontró el usuario autenticado");
        }
        return emp;
    }

    private PermisoAvanzadoResponseDTO toPermisoResponse(PermisoAvanzadoEntity entity) {
        return PermisoAvanzadoResponseDTO.builder()
                .id(entity.getId())
                .codigo(entity.getCodigo())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .build();
    }

    private AsignacionPermisoAvanzadoResponseDTO toAsignacionResponse(EmpleadoPermisoAvanzadoEntity entity) {
        EmpleadosEntity emp = entity.getEmpleado();
        String nombreCompleto = String.join(" ",
                StringUtils.defaultString(emp.getNombre()),
                StringUtils.defaultString(emp.getPrimerApellido()),
                StringUtils.defaultString(emp.getSegundoApellido())).trim();
        return AsignacionPermisoAvanzadoResponseDTO.builder()
                .id(entity.getId())
                .empleadoId(emp.getId())
                .nombreUsuario(emp.getNombreUsuario())
                .nombreEmpleado(nombreCompleto)
                .permisoId(entity.getPermiso().getId())
                .codigo(entity.getPermiso().getCodigo())
                .nombrePermiso(entity.getPermiso().getNombre())
                .asignadoPor(entity.getAsignadoPor())
                .build();
    }
}
