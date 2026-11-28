-- Cuadre de inventario: tipo (periodicidad) y categorías contadas (Buzón 10).
-- Los cuadres anteriores quedan sin tipo y sin categorías (= todo el almacén).
-- Idempotente para poder correr sobre bases con tablas creadas fuera de Flyway.
ALTER TABLE cuadres_inventario ADD COLUMN IF NOT EXISTS tipo VARCHAR(20);

CREATE TABLE IF NOT EXISTS cuadre_inventario_categorias
(
    cuadre_id    UUID NOT NULL,
    categoria_id UUID NOT NULL,
    CONSTRAINT pk_cuadre_inventario_categorias PRIMARY KEY (cuadre_id, categoria_id)
);

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_cuadre_categorias_cuadre') THEN
        ALTER TABLE cuadre_inventario_categorias ADD CONSTRAINT fk_cuadre_categorias_cuadre
            FOREIGN KEY (cuadre_id) REFERENCES cuadres_inventario (id);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_cuadre_categorias_categoria') THEN
        ALTER TABLE cuadre_inventario_categorias ADD CONSTRAINT fk_cuadre_categorias_categoria
            FOREIGN KEY (categoria_id) REFERENCES categoria_producto (id);
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_cuadre_categorias_categoria ON cuadre_inventario_categorias (categoria_id);
CREATE INDEX IF NOT EXISTS idx_cuadres_inventario_tipo ON cuadres_inventario (tipo);
