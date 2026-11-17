-- Un proveedor puede tener varios tipos de proveedor (ProveedorEntity.tiposProveedor, @ManyToMany).
-- La tabla intermedia proveedor_tipos no se creaba en ninguna migración (ddl-auto=none), así que
-- se crea si no existe y se copian los tipos que ya tenían los proveedores en la columna anterior
-- proveedores.tipo_proveedor_entity_id. La columna anterior se conserva.
CREATE TABLE IF NOT EXISTS proveedor_tipos
(
    proveedor_id      UUID NOT NULL,
    tipo_proveedor_id UUID NOT NULL,
    CONSTRAINT pk_proveedor_tipos PRIMARY KEY (proveedor_id, tipo_proveedor_id),
    CONSTRAINT fk_proveedor_tipos_proveedor FOREIGN KEY (proveedor_id) REFERENCES proveedores (id),
    CONSTRAINT fk_proveedor_tipos_tipo FOREIGN KEY (tipo_proveedor_id) REFERENCES tipos_proveedores (id)
);

DO $$
BEGIN
    IF EXISTS (SELECT 1
               FROM information_schema.columns
               WHERE table_schema = current_schema()
                 AND table_name = 'proveedores'
                 AND column_name = 'tipo_proveedor_entity_id') THEN
        INSERT INTO proveedor_tipos (proveedor_id, tipo_proveedor_id)
        SELECT p.id, p.tipo_proveedor_entity_id
        FROM proveedores p
        WHERE p.tipo_proveedor_entity_id IS NOT NULL
          AND NOT EXISTS (SELECT 1
                          FROM proveedor_tipos pt
                          WHERE pt.proveedor_id = p.id
                            AND pt.tipo_proveedor_id = p.tipo_proveedor_entity_id);
    END IF;
END $$;
