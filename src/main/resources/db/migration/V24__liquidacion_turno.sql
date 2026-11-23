-- Turno de la liquidación (MATUTINO, VESPERTINO o DOMINICAL); solo se captura en rutas de autoservicio.
ALTER TABLE liquidacion ADD COLUMN IF NOT EXISTS turno VARCHAR(20);
