-- Reinicio de medidor con reemplazo: el medidor viejo se cierra con su última lectura real y queda
-- ligado al medidor que lo reemplaza (y viceversa), con la fecha y hora del reemplazo.
ALTER TABLE medidores
    ADD COLUMN IF NOT EXISTS medidor_reemplazado_por_id UUID,
    ADD COLUMN IF NOT EXISTS reemplaza_a_id             UUID,
    ADD COLUMN IF NOT EXISTS fecha_reemplazo            TIMESTAMP WITHOUT TIME ZONE;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_medidores_reemplazado_por') THEN
        ALTER TABLE medidores ADD CONSTRAINT fk_medidores_reemplazado_por
            FOREIGN KEY (medidor_reemplazado_por_id) REFERENCES medidores (id_medidor);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_medidores_reemplaza_a') THEN
        ALTER TABLE medidores ADD CONSTRAINT fk_medidores_reemplaza_a
            FOREIGN KEY (reemplaza_a_id) REFERENCES medidores (id_medidor);
    END IF;
END $$;
