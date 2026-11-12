package com.main.tancol.dashboard.service;

import com.main.tancol.dashboard.dto.DashboardKpiConfiguracionDTO;
import com.main.tancol.dashboard.dto.DashboardKpiPreferenciasDTO;
import com.main.tancol.dashboard.dto.DashboardKpiPreferenciasDTO.Kpis;
import com.main.tancol.dashboard.entities.DashboardKpiConfiguracionEntity;
import com.main.tancol.dashboard.exception.ConfiguracionKpiInvalidaException;
import com.main.tancol.dashboard.repositories.DashboardKpiConfiguracionRepository;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardKpiConfiguracionServiceTest {

    @Mock
    private DashboardKpiConfiguracionRepository repository;

    @AfterEach
    void limpiarSesion() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getSinRegistroDevuelveDefaultsYNoPersiste() {
        UUID empleadoId = autenticar();
        when(repository.findByEmpleadoId(empleadoId)).thenReturn(Optional.empty());

        DashboardKpiConfiguracionDTO respuesta = service().obtener();

        assertFalse(respuesta.getConfiguracion().getInlineKpis());
        assertTrue(respuesta.getConfiguracion().getKpis().getUnidadesVendidas());
        assertEquals("ranking", respuesta.getConfiguracion().getViews().getEmpleadosMetasCumplidas());
        verify(repository).findByEmpleadoId(empleadoId);
        verify(repository, never()).save(any());
    }

    @Test
    void getCompletaClavesQueFaltanEnUnaConfiguracionAntigua() {
        UUID empleadoId = autenticar();
        Kpis kpis = new Kpis();
        kpis.setCanceladas(false);
        DashboardKpiPreferenciasDTO parcial = new DashboardKpiPreferenciasDTO();
        parcial.setKpis(kpis);
        DashboardKpiConfiguracionEntity entity = new DashboardKpiConfiguracionEntity();
        entity.setEmpleadoId(empleadoId);
        entity.setConfiguracion(parcial);
        when(repository.findByEmpleadoId(empleadoId)).thenReturn(Optional.of(entity));

        DashboardKpiConfiguracionDTO respuesta = service().obtener();

        assertFalse(respuesta.getConfiguracion().getKpis().getCanceladas());
        assertTrue(respuesta.getConfiguracion().getKpis().getFacturadas());
        assertEquals("#ef4444", respuesta.getConfiguracion().getColors().getDevolucionesEquipos());
        verify(repository, never()).save(any());
    }

    @Test
    void putSoloGuardaLaConfiguracionDelUsuarioAutenticado() {
        UUID empleadoId = autenticar();
        when(repository.findByEmpleadoId(empleadoId)).thenReturn(Optional.empty());
        when(repository.save(any(DashboardKpiConfiguracionEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Kpis kpis = new Kpis();
        kpis.setUnidadesVendidas(false);
        DashboardKpiPreferenciasDTO entrada = new DashboardKpiPreferenciasDTO();
        entrada.setKpis(kpis);

        DashboardKpiConfiguracionDTO respuesta = service().guardar(entrada);

        ArgumentCaptor<DashboardKpiConfiguracionEntity> captor = ArgumentCaptor.forClass(DashboardKpiConfiguracionEntity.class);
        verify(repository).save(captor.capture());
        assertEquals(empleadoId, captor.getValue().getEmpleadoId());
        assertFalse(captor.getValue().isDeleted());
        assertFalse(respuesta.getConfiguracion().getKpis().getUnidadesVendidas());
        assertTrue(respuesta.getConfiguracion().getCharts().getTopClientes());
    }

    @Test
    void putReemplazaLaConfiguracionYNoConservaClavesOmitidas() {
        UUID empleadoId = autenticar();
        Kpis anteriores = new Kpis();
        anteriores.setCanceladas(false);
        DashboardKpiPreferenciasDTO previa = new DashboardKpiPreferenciasDTO();
        previa.setInlineKpis(true);
        previa.setKpis(anteriores);
        DashboardKpiConfiguracionEntity entity = new DashboardKpiConfiguracionEntity();
        entity.setEmpleadoId(empleadoId);
        entity.setConfiguracion(previa);
        when(repository.findByEmpleadoId(empleadoId)).thenReturn(Optional.of(entity));
        when(repository.save(any(DashboardKpiConfiguracionEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DashboardKpiConfiguracionDTO respuesta = service().guardar(new DashboardKpiPreferenciasDTO());

        assertFalse(respuesta.getConfiguracion().getInlineKpis());
        assertTrue(respuesta.getConfiguracion().getKpis().getCanceladas());
        assertEquals(empleadoId, entity.getEmpleadoId());
    }

    @Test
    void otroUsuarioConsultaSoloSuPropiaFila() {
        UUID empleadoA = UUID.randomUUID();
        UUID empleadoB = autenticar();
        when(repository.findByEmpleadoId(empleadoB)).thenReturn(Optional.empty());

        service().obtener();

        verify(repository).findByEmpleadoId(empleadoB);
        verify(repository, never()).findByEmpleadoId(empleadoA);
    }

    @Test
    void putInvalidoNoSeGuarda() {
        autenticar();
        DashboardKpiPreferenciasDTO.Views views = new DashboardKpiPreferenciasDTO.Views();
        views.setTopProductos("mapa");
        DashboardKpiPreferenciasDTO entrada = new DashboardKpiPreferenciasDTO();
        entrada.setViews(views);

        assertThrows(ConfiguracionKpiInvalidaException.class, () -> service().guardar(entrada));
        verify(repository, never()).save(any());
    }

    @Test
    void sinUsuarioAutenticadoResponde401() {
        ResponseStatusException error = assertThrows(ResponseStatusException.class, () -> service().obtener());

        assertEquals(HttpStatus.UNAUTHORIZED, error.getStatusCode());
        verifyNoInteractions(repository);
    }

    private DashboardKpiConfiguracionService service() {
        return new DashboardKpiConfiguracionService(repository, new DashboardKpiPreferenciasNormalizer());
    }

    private static UUID autenticar() {
        UUID empleadoId = UUID.randomUUID();
        EmpleadosEntity empleado = new EmpleadosEntity();
        empleado.setId(empleadoId);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(empleado, null, List.of()));
        return empleadoId;
    }
}
