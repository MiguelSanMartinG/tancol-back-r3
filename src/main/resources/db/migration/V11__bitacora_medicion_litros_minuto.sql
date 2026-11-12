ALTER TABLE bitacoras_medicion
    ADD COLUMN IF NOT EXISTS litros_minuto DECIMAL(19, 2);
