-- Cuadre de inventario por almacén: existencia del sistema congelada al abrir, cantidad física
-- contada, cierre con firma (usuario, fecha y hora) y opción de ajustar la existencia a lo contado.
CREATE SEQUENCE IF NOT EXISTS cuadres_inventario_folio_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS cuadres_inventario
(
    id              UUID                        NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE,
    updated_at      TIMESTAMP WITHOUT TIME ZONE,
    enabled         BOOLEAN                     NOT NULL DEFAULT TRUE,
    deleted         BOOLEAN                     NOT NULL DEFAULT FALSE,
    folio           VARCHAR(20),
    almacen_id      UUID                        NOT NULL,
    estado          VARCHAR(20)                 NOT NULL DEFAULT 'ABIERTO',
    creado_por_id   UUID,
    fecha_creacion  TIMESTAMP WITHOUT TIME ZONE,
    cerrado_por_id  UUID,
    fecha_cierre    TIMESTAMP WITHOUT TIME ZONE,
    ajuste_aplicado BOOLEAN                     NOT NULL DEFAULT FALSE,
    observaciones   VARCHAR(1000),
    id_imagen_firma VARCHAR(255),
    fecha_firma     TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_cuadres_inventario PRIMARY KEY (id),
    CONSTRAINT fk_cuadres_inventario_almacen FOREIGN KEY (almacen_id) REFERENCES almacenes (id),
    CONSTRAINT fk_cuadres_inventario_creado_por FOREIGN KEY (creado_por_id) REFERENCES empleados (id),
    CONSTRAINT fk_cuadres_inventario_cerrado_por FOREIGN KEY (cerrado_por_id) REFERENCES empleados (id)
);

CREATE INDEX IF NOT EXISTS idx_cuadres_inventario_almacen_estado ON cuadres_inventario (almacen_id, estado);

CREATE TABLE IF NOT EXISTS cuadres_inventario_partidas
(
    id               UUID                        NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    updated_at       TIMESTAMP WITHOUT TIME ZONE,
    enabled          BOOLEAN                     NOT NULL DEFAULT TRUE,
    deleted          BOOLEAN                     NOT NULL DEFAULT FALSE,
    cuadre_id        UUID                        NOT NULL,
    inventario_id    UUID,
    producto_id      UUID,
    producto         VARCHAR(255),
    sku              VARCHAR(255),
    unidad           VARCHAR(255),
    cantidad_sistema NUMERIC(19, 2)              NOT NULL,
    cantidad_fisica  NUMERIC(19, 2),
    CONSTRAINT pk_cuadres_inventario_partidas PRIMARY KEY (id),
    CONSTRAINT fk_cuadres_partidas_cuadre FOREIGN KEY (cuadre_id) REFERENCES cuadres_inventario (id),
    CONSTRAINT fk_cuadres_partidas_inventario FOREIGN KEY (inventario_id) REFERENCES inventarios (id)
);

CREATE INDEX IF NOT EXISTS idx_cuadres_partidas_cuadre ON cuadres_inventario_partidas (cuadre_id);

-- Módulo de permisos "Cuadre de Inventario" (ruta cuadre-inventario) dentro de Operación.
-- Los roles que hoy tienen permisos en Inventario General reciben los mismos en el módulo nuevo;
-- después se ajustan por rol en Configuración.
INSERT INTO modulos (id, created_at, updated_at, enabled, deleted, name, ruta_modulo, parent_id)
SELECT gen_random_uuid(), now(), now(), true, false, 'Cuadre de Inventario', 'cuadre-inventario', padre.id
FROM (SELECT id FROM modulos WHERE ruta_modulo = 'operacion' AND deleted = false LIMIT 1) padre
WHERE NOT EXISTS (SELECT 1 FROM modulos WHERE ruta_modulo = 'cuadre-inventario' AND deleted = false);

INSERT INTO perfiles (id_perfil, created_at, updated_at, enabled, deleted, module_id, role_id,
                      can_read, can_write, can_delete, can_update)
SELECT gen_random_uuid(), now(), now(), true, false, nuevo.id, p.role_id,
       p.can_read, p.can_write, p.can_delete, p.can_update
FROM perfiles p
JOIN modulos inv ON inv.id = p.module_id AND inv.ruta_modulo = 'inventario' AND inv.deleted = false
JOIN modulos nuevo ON nuevo.ruta_modulo = 'cuadre-inventario' AND nuevo.deleted = false
WHERE p.deleted = false
  AND p.role_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM perfiles e
                  WHERE e.module_id = nuevo.id AND e.role_id = p.role_id AND e.deleted = false);
