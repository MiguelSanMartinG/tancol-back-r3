-- Añadir columna year como VARCHAR a la tabla secuencia
ALTER TABLE secuencias
    ADD COLUMN year VARCHAR(4);

-- Actualizar la columna con el año actual (dinámico)
-- Para MySQL:
UPDATE secuencias
SET year = EXTRACT(YEAR FROM NOW())::VARCHAR;