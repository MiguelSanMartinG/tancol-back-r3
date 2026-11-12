package com.main.tancol.logistica.service.bitacoras;

import com.main.tancol.common.exceptions.exeption.GenericExeption;
import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.empleados.helpers.EmpleadoContext;
import com.main.tancol.empleados.repositories.EmpleadosRepository;
import com.main.tancol.logistica.dto.request.MermaVentaTrayectoRequestDTO;
import com.main.tancol.logistica.entities.bitacora.MermaVentaTrayectoEntity;
import com.main.tancol.logistica.entities.bitacora.VentaTrayectoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MermaVentaTrayectoService {

    private final EmpleadosRepository empleadosRepository;

    public VentaTrayectoEntity create(VentaTrayectoEntity venta, Set<MermaVentaTrayectoRequestDTO> mermas) {
        if (mermas == null || mermas.isEmpty()) {
            return venta;
        }
        for (MermaVentaTrayectoRequestDTO dto : mermas) {
            venta.agregarMerma(toEntity(dto));
        }
        return venta;
    }

    public void replace(VentaTrayectoEntity venta, Set<MermaVentaTrayectoRequestDTO> mermas) {
        if (venta.getMermas() == null) {
            venta.setMermas(new LinkedHashSet<>());
        } else {
            venta.getMermas().clear();
        }
        create(venta, mermas);
    }

    private MermaVentaTrayectoEntity toEntity(MermaVentaTrayectoRequestDTO dto) {
        if (dto.getCantidad() == null || dto.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
            throw new GenericExeption("La cantidad de merma debe ser mayor a 0");
        }

        boolean rotoPorMaquina = Boolean.TRUE.equals(dto.getRotoPorMaquina());
        String idImagenFirma = blankToNull(dto.getIdImagenFirma());

        EmpleadosEntity responsable = null;
        EmpleadosEntity firmante;

        if (rotoPorMaquina) {
            if (idImagenFirma == null) {
                throw new GenericExeption("La firma es obligatoria cuando el roto es por máquina");
            }
            firmante = EmpleadoContext.getEmpleadoActual();
            if (firmante == null || firmante.getId() == null) {
                throw new GenericExeption("No se pudo identificar al usuario en sesión para firmar el roto por máquina");
            }
            firmante = empleadosRepository.findById(firmante.getId())
                    .orElseThrow(() -> new GenericExeption("No se pudo identificar al usuario en sesión para firmar el roto por máquina"));
        } else {
            if (dto.getIdEmpleado() == null) {
                throw new GenericExeption("El responsable es obligatorio cuando el roto no es por máquina");
            }
            responsable = empleadosRepository.findById(dto.getIdEmpleado())
                    .orElseThrow(() -> new IDNotFound("No se encontró al empleado responsable de la merma"));
            firmante = responsable;
        }

        return MermaVentaTrayectoEntity.builder()
                .cantidad(dto.getCantidad())
                .rotoPorMaquina(rotoPorMaquina)
                .empleadoResponsable(responsable)
                .empleadoFirmante(firmante)
                .idImagenFirma(idImagenFirma)
                .build();
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
