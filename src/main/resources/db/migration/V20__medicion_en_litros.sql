-- Las lecturas de medidores se guardan en LITROS (antes en m3 = litros / 1000).
-- Con m3 y 2 decimales solo cabían decenas de litros: 9.9 L o 5 L quedaban en 0.01 m3 (10 L).
-- Los m3 ahora solo se calculan para mostrarse en pantalla.
-- liquidado, quedado_en_pipa, litros_minuto y alerta_consumo_litros ya estaban en litros.
-- Algunas columnas se crearon fuera de las migraciones, por eso se revisa que existan.
DO $$
DECLARE
    col RECORD;
    tiene_liquidado BOOLEAN;
    tiene_quedado BOOLEAN;
    tiene_consumo BOOLEAN;
BEGIN
    FOR col IN
        SELECT * FROM (VALUES
            ('bitacoras_medicion', 'medida_actual'),
            ('bitacoras_medicion', 'medida_inicial'),
            ('bitacoras_medicion', 'consumo'),
            ('medidores', 'medicion_actual')
        ) AS t(tabla, columna)
    LOOP
        IF EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_schema = current_schema()
                     AND table_name = col.tabla AND column_name = col.columna) THEN
            EXECUTE format('UPDATE %I SET %I = %I * 1000 WHERE %I IS NOT NULL',
                           col.tabla, col.columna, col.columna, col.columna);
        END IF;
    END LOOP;

    -- La diferencia se calculaba con el consumo en m3 contra liquidado y quedado en litros;
    -- se recalcula con todo en litros.
    SELECT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = current_schema()
                   AND table_name = 'bitacoras_medicion' AND column_name = 'consumo') INTO tiene_consumo;
    SELECT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = current_schema()
                   AND table_name = 'bitacoras_medicion' AND column_name = 'liquidado') INTO tiene_liquidado;
    SELECT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = current_schema()
                   AND table_name = 'bitacoras_medicion' AND column_name = 'quedado_en_pipa') INTO tiene_quedado;
    IF tiene_consumo THEN
        EXECUTE format(
            'UPDATE bitacoras_medicion SET diferencia = COALESCE(consumo, medida_actual - medida_inicial) - %s - %s '
            || 'WHERE medida_actual IS NOT NULL AND medida_inicial IS NOT NULL',
            CASE WHEN tiene_liquidado THEN 'COALESCE(liquidado, 0)' ELSE '0' END,
            CASE WHEN tiene_quedado THEN 'COALESCE(quedado_en_pipa, 0)' ELSE '0' END);
    END IF;
END $$;
