package com.main.tancol.medidores.dto.response;

import com.main.tancol.medidores.entities.MedidoresEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

/** Referencia corta a un medidor (reemplazo / reemplazado). */
@Getter
@AllArgsConstructor
public class MedidorRefDTO {
    private UUID idMedidor;
    private String nombre;
    private String alias;

    public static MedidorRefDTO de(MedidoresEntity medidor) {
        return medidor == null ? null : new MedidorRefDTO(medidor.getIdMedidor(), medidor.getNombre(), medidor.getAlias());
    }
}
