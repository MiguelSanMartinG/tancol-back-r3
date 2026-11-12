DROP TABLE secuencia CASCADE;

ALTER TABLE orden_entity
    DROP COLUMN comentarios;

ALTER TABLE orden_entity
    DROP COLUMN recepcion;

ALTER TABLE orden_entity
    ALTER COLUMN total_orden TYPE DECIMAL USING (total_orden::DECIMAL);