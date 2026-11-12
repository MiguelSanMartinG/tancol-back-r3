ALTER TABLE liquidacion_partidas
    ADD COLUMN IF NOT EXISTS motivo_cancelacion   VARCHAR(500),
    ADD COLUMN IF NOT EXISTS usuario_cancelacion  VARCHAR(255),
    ADD COLUMN IF NOT EXISTS fecha_cancelacion    TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE liquidacion
    ADD COLUMN IF NOT EXISTS motivo_cancelacion   VARCHAR(500),
    ADD COLUMN IF NOT EXISTS usuario_cancelacion  VARCHAR(255),
    ADD COLUMN IF NOT EXISTS fecha_cancelacion    TIMESTAMP WITHOUT TIME ZONE;
