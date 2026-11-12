package com.main.tancol.medidores.service;

import com.main.tancol.medidores.entities.BitacoraMedicionEntity;
import com.main.tancol.medidores.entities.MedidoresEntity;
import com.main.tancol.medidores.repositories.BitacoraMedicionRepository;
import com.main.tancol.medidores.repositories.MedidoresRepository;
import com.main.tancol.medidores.util.MedidorEstadoContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MedidorEstadoService {

    private final BitacoraMedicionRepository bitacoraMedicionRepository;
    private final MedidoresRepository medidoresRepository;

    public MedidorEstadoContext construirContexto(Collection<MedidoresEntity> medidores) {
        if (medidores == null || medidores.isEmpty()) {
            return MedidorEstadoContext.VACIO;
        }

        Set<UUID> ids = new HashSet<>();
        Set<UUID> idsMedidores = new HashSet<>();
        for (MedidoresEntity medidor : medidores) {
            ids.add(medidor.getIdMedidor());
            idsMedidores.add(medidor.getIdMedidor());
            if (medidor.getMedidoresHijos() != null) {
                medidor.getMedidoresHijos().forEach(hijo -> ids.add(hijo.getIdMedidor()));
            }
        }

        if (!idsMedidores.isEmpty()) {
            medidoresRepository.findByMedidorPadre_IdMedidorIn(idsMedidores)
                    .forEach(hijo -> ids.add(hijo.getIdMedidor()));
        }

        if (ids.isEmpty()) {
            return MedidorEstadoContext.VACIO;
        }

        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        LocalDateTime finDia = inicioDia.plusDays(1);

        List<BitacoraMedicionEntity> bitacorasDelDia = bitacoraMedicionRepository
                .findByMedidor_IdMedidorInAndCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtDesc(
                        ids, inicioDia, finDia);

        Map<UUID, Boolean> bitacoraHoy = new HashMap<>();
        Map<UUID, BigDecimal> consumoHoy = new HashMap<>();
        for (UUID id : ids) {
            bitacoraHoy.put(id, false);
            consumoHoy.put(id, null);
        }

        for (BitacoraMedicionEntity bitacora : bitacorasDelDia) {
            UUID medidorId = bitacora.getMedidor().getIdMedidor();
            if (!Boolean.TRUE.equals(bitacoraHoy.get(medidorId))) {
                bitacoraHoy.put(medidorId, true);
                consumoHoy.put(medidorId, bitacora.getConsumo());
            }
        }

        return new MedidorEstadoContext(bitacoraHoy, consumoHoy);
    }
}
