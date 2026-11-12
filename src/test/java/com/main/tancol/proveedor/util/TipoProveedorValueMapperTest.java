package com.main.tancol.proveedor.util;

import com.main.tancol.proveedor.dto.response.TipoProveedorResponseDTO;
import com.main.tancol.proveedor.entity.ProveedorEntity;
import com.main.tancol.proveedor.entity.TipoProveedorEntity;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TipoProveedorValueMapperTest {

    @Test
    void elListadoNoRecorreLosProveedoresDelTipo() {
        TipoProveedorEntity tipo = TipoProveedorEntity.builder()
                .id(UUID.randomUUID())
                .nombre("Gas")
                .alias("GAS")
                .build();
        tipo.setProveedorEntities(new ArrayList<>() {
            @Override
            public Stream<ProveedorEntity> stream() {
                throw new AssertionError("no debe recorrer proveedores");
            }
        });

        TipoProveedorResponseDTO dto = TipoProveedorValueMapper.TipoProveedorEntityToResponse(tipo);

        assertEquals("Gas", dto.getNombre());
        assertTrue(dto.getProveedores().isEmpty());
    }
}
