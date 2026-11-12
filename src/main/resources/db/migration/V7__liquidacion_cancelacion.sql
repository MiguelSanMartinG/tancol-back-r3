ALTER TABLE liquidacion_partidas
    ADD COLUMN IF NOT EXISTS cancelada BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE liquidacion_partidas SET cancelada = FALSE WHERE cancelada IS NULL;
