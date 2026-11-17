-- Firma de quien recibe en la salida de material (orden interna).
-- Guarda el nombre del archivo de la imagen subida y la fecha/hora en que se firmó. Es opcional.
ALTER TABLE ordenes_internas_history
    ADD COLUMN IF NOT EXISTS id_imagen_firma_recibe VARCHAR(255),
    ADD COLUMN IF NOT EXISTS fecha_firma_recibe TIMESTAMP WITHOUT TIME ZONE;
