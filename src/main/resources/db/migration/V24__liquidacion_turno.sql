-- Turno (MATUTINO, VESPERTINO o DOMINICAL) de la liquidación y de la partida de la bitácora de ventas;
-- solo aplica en rutas de autoservicio. La liquidación generada desde la bitácora toma el turno de la partida.
ALTER TABLE liquidacion ADD COLUMN IF NOT EXISTS turno VARCHAR(20);
ALTER TABLE bitacora_rutas_ventas_trayectos ADD COLUMN IF NOT EXISTS turno VARCHAR(20);
