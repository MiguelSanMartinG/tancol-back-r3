package com.main.tancol.dashboard.service;

import com.main.tancol.dashboard.dto.DashboardKpiConfiguracionDTO;
import com.main.tancol.dashboard.dto.DashboardKpiPreferenciasDTO;
import com.main.tancol.dashboard.entities.DashboardKpiConfiguracionEntity;
import com.main.tancol.dashboard.exception.ConfiguracionKpiInvalidaException;
import com.main.tancol.dashboard.repositories.DashboardKpiConfiguracionRepository;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.empleados.helpers.EmpleadoContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardKpiConfiguracionService {

    private final DashboardKpiConfiguracionRepository repository;
    private final DashboardKpiPreferenciasNormalizer normalizer;

    @Transactional(readOnly = true)
    public DashboardKpiConfiguracionDTO obtener() {
        UUID empleadoId = empleadoAutenticado();
        DashboardKpiPreferenciasDTO guardada = repository.findByEmpleadoId(empleadoId)
                .map(DashboardKpiConfiguracionEntity::getConfiguracion)
                .orElse(null);
        return envolver(normalizer.normalizar(guardada));
    }

    @Transactional
    public DashboardKpiConfiguracionDTO guardar(DashboardKpiPreferenciasDTO preferencias) {
        if (preferencias == null) {
            throw new ConfiguracionKpiInvalidaException("La configuración es obligatoria");
        }
        UUID empleadoId = empleadoAutenticado();
        DashboardKpiPreferenciasDTO normalizada = normalizer.normalizar(preferencias);
        DashboardKpiConfiguracionEntity entity = repository.findByEmpleadoId(empleadoId)
                .orElseGet(DashboardKpiConfiguracionEntity::new);
        entity.setEmpleadoId(empleadoId);
        entity.setConfiguracion(normalizada);
        entity.setEnabled(true);
        entity.setDeleted(false);
        repository.save(entity);
        return envolver(normalizada);
    }

    private UUID empleadoAutenticado() {
        EmpleadosEntity empleado = EmpleadoContext.getEmpleadoActual();
        if (empleado == null || empleado.getId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No se encontró el usuario autenticado");
        }
        return empleado.getId();
    }

    private static DashboardKpiConfiguracionDTO envolver(DashboardKpiPreferenciasDTO preferencias) {
        return DashboardKpiConfiguracionDTO.builder()
                .configuracion(preferencias)
                .build();
    }
}
