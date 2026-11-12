CREATE SEQUENCE empleado_id_externo_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE cliente_id_externo_seq START WITH 1 INCREMENT BY 1;

-- AL AÑADIR UNA NUEVA SECUENCIA AÑADIR a columna, abr
CREATE TABLE IF NOT EXISTS secuencias();

ALTER TABLE secuencias
    ADD COLUMN IF NOT EXISTS secuencia_ordenes BIGINT,
    ADD COLUMN IF NOT EXISTS secuencia_liquidacion BIGINT,
    ADD COLUMN IF NOT EXISTS secuencia_ord_interna BIGINT,
    ADD COLUMN IF NOT EXISTS secuencia_bit_venta BIGINT;

ALTER TABLE secuencias
    ALTER COLUMN secuencia_ordenes SET DEFAULT 1;
ALTER TABLE secuencias
    ALTER COLUMN secuencia_liquidacion SET DEFAULT 1;
ALTER TABLE secuencias
    ALTER COLUMN secuencia_ord_interna SET DEFAULT 1;
ALTER TABLE secuencias
    ALTER COLUMN secuencia_bit_venta SET DEFAULT 1;

CREATE OR REPLACE FUNCTION generar_folio(num integer)
    RETURNS text
    LANGUAGE plpgsql
AS
$function$
DECLARE
    year_actual  VARCHAR(2);
    valor_actual INT;
    nuevo_id     TEXT;
    columna      TEXT;
    abr          TEXT;
BEGIN
    year_actual := TO_CHAR(CURRENT_DATE, 'YY');
    LOCK TABLE secuencias IN ROW EXCLUSIVE MODE;

    columna := CASE num
                   WHEN 1 THEN 'secuencia_ordenes'
                   WHEN 2 THEN 'secuencia_liquidacion'
                   WHEN 3 THEN 'secuencia_ord_interna'
                   WHEN 4 THEN 'secuencia_bit_venta'
        END;
    abr := CASE num
               WHEN 1 THEN 'OC-'
               WHEN 2 THEN 'LI-'
               WHEN 3 THEN 'OI-'
               WHEN 4 THEN 'VT-'
        END;

    IF EXISTS (SELECT 1 FROM secuencias WHERE year = year_actual) THEN
        EXECUTE format('SELECT %I FROM secuencias WHERE year = $1', columna)
            INTO valor_actual
            USING year_actual;

        valor_actual := valor_actual + 1;

        EXECUTE format('UPDATE secuencias SET %I = $1 WHERE year = $2', columna)
            USING valor_actual, year_actual;
    ELSE
        valor_actual := 1;
        EXECUTE format('INSERT INTO secuencias (year, %I) VALUES ($1, $2)', columna)
            USING year_actual, valor_actual;
    END IF;

    nuevo_id := abr || year_actual || '-' || LPAD(valor_actual::TEXT, 2, '0');
    RETURN nuevo_id;
END;
$function$;