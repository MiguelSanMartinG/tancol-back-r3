CREATE TABLE IF NOT EXISTS meta_venta_ruta
(
    id                    UUID                        NOT NULL,
    created_at            TIMESTAMP WITHOUT TIME ZONE,
    updated_at            TIMESTAMP WITHOUT TIME ZONE,
    enabled               BOOLEAN                     NOT NULL DEFAULT TRUE,
    deleted               BOOLEAN                     NOT NULL DEFAULT FALSE,
    ruta_id               UUID                        NOT NULL,
    producto_comercial_id UUID                        NOT NULL,
    cantidad_minima       NUMERIC(19, 2)              NOT NULL,
    periodo               VARCHAR(50)                 NOT NULL DEFAULT 'SEMANAL',
    activa                BOOLEAN                     NOT NULL DEFAULT TRUE,
    fecha_inicio          TIMESTAMP WITHOUT TIME ZONE,
    fecha_cierre          TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_meta_venta_ruta PRIMARY KEY (id),
    CONSTRAINT fk_meta_venta_ruta_ruta FOREIGN KEY (ruta_id) REFERENCES rutas (id),
    CONSTRAINT fk_meta_venta_ruta_producto FOREIGN KEY (producto_comercial_id) REFERENCES productos_comerciales (id)
);

CREATE INDEX IF NOT EXISTS idx_meta_venta_ruta_ruta_producto
    ON meta_venta_ruta (ruta_id, producto_comercial_id);
