package com.main.tancol.common.service;

import com.main.tancol.common.exeptions.IDNotFound;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 *                    WHEN 1 THEN 'secuencia_ordenes'
 *                    WHEN 2 THEN 'secuencia_liquidacion'
 *                    WHEN 3 THEN 'secuencia_ord_interna'
 *                    WHEN 4 THEN 'secuencia_bit_venta'
 */


@Service
@RequiredArgsConstructor
public class FolioService {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Genera un folio utilizando una función SQL llamada {@code generar_folio} en la base de datos.
     *
     * Este método ejecuta una consulta con {@code jdbcTemplate} para obtener el resultado de
     * {@code generar_folio(?)} pasando el parámetro {@code n} como argumento.
     *  1.- Ordenes
     *  2.- Liquidacion por ruta
     *  3.- orden interna
     *  4.- Bitacoras de Venta por ruta
     * @param n el valor numérico que se enviará a la función {@code generar_folio} en la base de datos
     * @return el resultado de la función SQL como una {@code String}
     */

    public String generarFolio(Integer n) {
        try {
            if (n < 1) {
                throw new RuntimeException("Error al proporcionar un folio valido");
            }
            return jdbcTemplate.queryForObject("SELECT generar_folio(?)", String.class, n);
        } catch (Exception e) {
            throw new IDNotFound("Hubo un error al generar el folio" + e.getMessage());
        }

    }
}
