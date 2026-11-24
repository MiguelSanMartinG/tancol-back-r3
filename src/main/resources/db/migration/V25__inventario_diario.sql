-- Inventario diario de insumos y solicitudes de stock (Buzón 1 a 5).
-- Áreas = departamentos (con su almacén); el inventario físico vive solo en el almacén (tabla inventario).
-- Cada área tiene un cupo diario por insumo; las partidas descuentan del cupo y del almacén.
-- Todo es idempotente para poder correr sobre bases con tablas creadas fuera de Flyway.

-- ---------------------------------------------------------------- catálogos (Buzón 5)
ALTER TABLE departamentos ADD COLUMN IF NOT EXISTS almacen_id UUID;
ALTER TABLE almacenes ADD COLUMN IF NOT EXISTS responsable_id UUID;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_departamentos_almacen') THEN
        ALTER TABLE departamentos ADD CONSTRAINT fk_departamentos_almacen FOREIGN KEY (almacen_id) REFERENCES almacenes (id);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_almacenes_responsable') THEN
        ALTER TABLE almacenes ADD CONSTRAINT fk_almacenes_responsable FOREIGN KEY (responsable_id) REFERENCES empleados (id);
    END IF;
END $$;

-- ---------------------------------------------------------------- permisos avanzados (Buzón 4)
-- Las tablas pueden no existir en bases creadas solo con Flyway.
CREATE TABLE IF NOT EXISTS permisos_avanzados
(
    id          UUID         NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    enabled     BOOLEAN      NOT NULL DEFAULT true,
    deleted     BOOLEAN      NOT NULL DEFAULT false,
    codigo      VARCHAR(100) NOT NULL,
    nombre      VARCHAR(255) NOT NULL,
    descripcion VARCHAR(500),
    CONSTRAINT pk_permisos_avanzados PRIMARY KEY (id),
    CONSTRAINT uk_permisos_avanzados_codigo UNIQUE (codigo)
);

CREATE TABLE IF NOT EXISTS empleado_permiso_avanzado
(
    id                  UUID    NOT NULL,
    created_at          TIMESTAMP WITHOUT TIME ZONE,
    updated_at          TIMESTAMP WITHOUT TIME ZONE,
    enabled             BOOLEAN NOT NULL DEFAULT true,
    deleted             BOOLEAN NOT NULL DEFAULT false,
    empleado_id         UUID    NOT NULL,
    permiso_avanzado_id UUID    NOT NULL,
    asignado_por        VARCHAR(255),
    CONSTRAINT pk_empleado_permiso_avanzado PRIMARY KEY (id),
    CONSTRAINT fk_epa_empleado FOREIGN KEY (empleado_id) REFERENCES empleados (id),
    CONSTRAINT fk_epa_permiso FOREIGN KEY (permiso_avanzado_id) REFERENCES permisos_avanzados (id)
);

INSERT INTO permisos_avanzados (id, created_at, updated_at, enabled, deleted, codigo, nombre, descripcion)
SELECT gen_random_uuid(), now(), now(), true, false, t.codigo, t.nombre, t.descripcion
FROM (VALUES
    ('RESPONSABLE_AREA', 'Responsable de área',
     'Inventario diario: confirma el cupo del día de su departamento y administra sus tarjetas.'),
    ('RESPONSABLE_ALMACEN', 'Responsable de almacén',
     'Inventario diario: acepta o rechaza solicitudes de stock de todos los almacenes.')
) AS t(codigo, nombre, descripcion)
WHERE NOT EXISTS (SELECT 1 FROM permisos_avanzados p WHERE p.codigo = t.codigo);

-- ---------------------------------------------------------------- inventario diario (Buzón 1 y 2)
CREATE SEQUENCE IF NOT EXISTS inv_diario_partida_folio_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS inv_diario_solicitud_folio_seq START WITH 1 INCREMENT BY 1;

-- Tarjeta: cupo base diario de un insumo para un área.
CREATE TABLE IF NOT EXISTS inv_diario_cupos
(
    id              UUID           NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE,
    updated_at      TIMESTAMP WITHOUT TIME ZONE,
    enabled         BOOLEAN        NOT NULL DEFAULT true,
    deleted         BOOLEAN        NOT NULL DEFAULT false,
    departamento_id UUID           NOT NULL,
    almacen_id      UUID           NOT NULL,
    producto_id     UUID           NOT NULL,
    por_defecto     BOOLEAN        NOT NULL DEFAULT false,
    base            NUMERIC(19, 2) NOT NULL,
    minimo          NUMERIC(19, 2) NOT NULL,
    CONSTRAINT pk_inv_diario_cupos PRIMARY KEY (id),
    CONSTRAINT fk_idc_departamento FOREIGN KEY (departamento_id) REFERENCES departamentos (id),
    CONSTRAINT fk_idc_almacen FOREIGN KEY (almacen_id) REFERENCES almacenes (id),
    CONSTRAINT fk_idc_producto FOREIGN KEY (producto_id) REFERENCES productos_basicos (id)
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_inv_diario_cupo_area_insumo
    ON inv_diario_cupos (departamento_id, producto_id) WHERE deleted = false;

-- Estado del cupo en un día (inicial al confirmar, ampliado por solicitudes, consumido por partidas).
CREATE TABLE IF NOT EXISTS inv_diario_cupo_dia
(
    id         UUID           NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    enabled    BOOLEAN        NOT NULL DEFAULT true,
    deleted    BOOLEAN        NOT NULL DEFAULT false,
    cupo_id    UUID           NOT NULL,
    fecha      DATE           NOT NULL,
    inicial    NUMERIC(19, 2) NOT NULL DEFAULT 0,
    ampliado   NUMERIC(19, 2) NOT NULL DEFAULT 0,
    consumido  NUMERIC(19, 2) NOT NULL DEFAULT 0,
    faltante   NUMERIC(19, 2) NOT NULL DEFAULT 0,
    CONSTRAINT pk_inv_diario_cupo_dia PRIMARY KEY (id),
    CONSTRAINT fk_idcd_cupo FOREIGN KEY (cupo_id) REFERENCES inv_diario_cupos (id),
    CONSTRAINT uk_inv_diario_cupo_dia UNIQUE (cupo_id, fecha)
);

CREATE TABLE IF NOT EXISTS inv_diario_confirmaciones
(
    id              UUID NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE,
    updated_at      TIMESTAMP WITHOUT TIME ZONE,
    enabled         BOOLEAN NOT NULL DEFAULT true,
    deleted         BOOLEAN NOT NULL DEFAULT false,
    departamento_id UUID NOT NULL,
    fecha           DATE NOT NULL,
    confirmado_por  UUID,
    confirmado_en   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_inv_diario_confirmaciones PRIMARY KEY (id),
    CONSTRAINT fk_idconf_departamento FOREIGN KEY (departamento_id) REFERENCES departamentos (id),
    CONSTRAINT fk_idconf_empleado FOREIGN KEY (confirmado_por) REFERENCES empleados (id),
    CONSTRAINT uk_inv_diario_confirmacion UNIQUE (departamento_id, fecha)
);

CREATE TABLE IF NOT EXISTS inv_diario_partidas
(
    id               UUID           NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    updated_at       TIMESTAMP WITHOUT TIME ZONE,
    enabled          BOOLEAN        NOT NULL DEFAULT true,
    deleted          BOOLEAN        NOT NULL DEFAULT false,
    folio            VARCHAR(20)    NOT NULL,
    fecha            TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    departamento_id  UUID           NOT NULL,
    ruta_id          UUID,
    producto_id      UUID           NOT NULL,
    cantidad         NUMERIC(19, 2) NOT NULL,
    concepto         VARCHAR(20)    NOT NULL,
    comentario       VARCHAR(500),
    roto_por_id      UUID,
    roto_por_maquina BOOLEAN        NOT NULL DEFAULT false,
    usuario_id       UUID,
    firmante_id      UUID,
    id_imagen_firma  VARCHAR(300),
    fecha_firma      TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_inv_diario_partidas PRIMARY KEY (id),
    CONSTRAINT fk_idp_departamento FOREIGN KEY (departamento_id) REFERENCES departamentos (id),
    CONSTRAINT fk_idp_ruta FOREIGN KEY (ruta_id) REFERENCES rutas (id),
    CONSTRAINT fk_idp_producto FOREIGN KEY (producto_id) REFERENCES productos_basicos (id),
    CONSTRAINT fk_idp_roto_por FOREIGN KEY (roto_por_id) REFERENCES empleados (id),
    CONSTRAINT fk_idp_usuario FOREIGN KEY (usuario_id) REFERENCES empleados (id),
    CONSTRAINT fk_idp_firmante FOREIGN KEY (firmante_id) REFERENCES empleados (id)
);
CREATE INDEX IF NOT EXISTS idx_inv_diario_partidas_area_fecha ON inv_diario_partidas (departamento_id, fecha);

CREATE TABLE IF NOT EXISTS inv_diario_solicitudes
(
    id                  UUID           NOT NULL,
    created_at          TIMESTAMP WITHOUT TIME ZONE,
    updated_at          TIMESTAMP WITHOUT TIME ZONE,
    enabled             BOOLEAN        NOT NULL DEFAULT true,
    deleted             BOOLEAN        NOT NULL DEFAULT false,
    folio               VARCHAR(20)    NOT NULL,
    departamento_id     UUID           NOT NULL,
    producto_id         UUID           NOT NULL,
    almacen_id          UUID           NOT NULL,
    cantidad_solicitada NUMERIC(19, 2) NOT NULL,
    nota                VARCHAR(500),
    solicitado_por      UUID,
    fecha_solicitud     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    estatus             VARCHAR(20)    NOT NULL,
    cantidad_a_entregar NUMERIC(19, 2),
    comentario          VARCHAR(500),
    confirmado_por      UUID,
    fecha_confirmacion  TIMESTAMP WITHOUT TIME ZONE,
    cantidad_recibida   NUMERIC(19, 2),
    recibido_por        UUID,
    fecha_recepcion     TIMESTAMP WITHOUT TIME ZONE,
    id_imagen_firma     VARCHAR(300),
    CONSTRAINT pk_inv_diario_solicitudes PRIMARY KEY (id),
    CONSTRAINT fk_ids_departamento FOREIGN KEY (departamento_id) REFERENCES departamentos (id),
    CONSTRAINT fk_ids_producto FOREIGN KEY (producto_id) REFERENCES productos_basicos (id),
    CONSTRAINT fk_ids_almacen FOREIGN KEY (almacen_id) REFERENCES almacenes (id),
    CONSTRAINT fk_ids_solicitado FOREIGN KEY (solicitado_por) REFERENCES empleados (id),
    CONSTRAINT fk_ids_confirmado FOREIGN KEY (confirmado_por) REFERENCES empleados (id),
    CONSTRAINT fk_ids_recibido FOREIGN KEY (recibido_por) REFERENCES empleados (id)
);
CREATE INDEX IF NOT EXISTS idx_inv_diario_solicitudes_area ON inv_diario_solicitudes (departamento_id, estatus);

-- ---------------------------------------------------------------- módulos (Buzón 4)
-- Bajo Operación, con los mismos permisos por rol que tiene Inventario general.
INSERT INTO modulos (id, created_at, updated_at, enabled, deleted, name, ruta_modulo, parent_id)
SELECT gen_random_uuid(), now(), now(), true, false, m.nombre, m.ruta, padre.id
FROM (VALUES ('Inventario diario de insumos', 'inventario-diario'),
             ('Confirmación de solicitudes de stock', 'confirmacion-stock')) AS m(nombre, ruta)
CROSS JOIN (SELECT id FROM modulos WHERE ruta_modulo = 'operacion' AND deleted = false LIMIT 1) padre
WHERE NOT EXISTS (SELECT 1 FROM modulos x WHERE x.ruta_modulo = m.ruta AND x.deleted = false);

INSERT INTO perfiles (id_perfil, created_at, updated_at, enabled, deleted, module_id, role_id,
                      can_read, can_write, can_delete, can_update)
SELECT gen_random_uuid(), now(), now(), true, false, nuevo.id, p.role_id,
       p.can_read, p.can_write, p.can_delete, p.can_update
FROM perfiles p
JOIN modulos inv ON inv.id = p.module_id AND inv.ruta_modulo = 'inventario' AND inv.deleted = false
JOIN modulos nuevo ON nuevo.ruta_modulo IN ('inventario-diario', 'confirmacion-stock') AND nuevo.deleted = false
WHERE p.deleted = false
  AND p.role_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM perfiles e
                  WHERE e.module_id = nuevo.id AND e.role_id = p.role_id AND e.deleted = false);

-- ---------------------------------------------------------------- notificaciones (Buzón 3)
DO $$
DECLARE
    restriccion RECORD;
    habia_check BOOLEAN := false;
BEGIN
    IF to_regclass('public.notificacion_tipos') IS NOT NULL THEN
        INSERT INTO notificacion_tipos (id, created_at, updated_at, enabled, deleted, codigo, nombre, descripcion)
        SELECT gen_random_uuid(), now(), now(), true, false, t.codigo, t.nombre, t.descripcion
        FROM (VALUES
            ('SOLICITUD_STOCK_NUEVA', 'Nueva solicitud de stock',
             'Avisa al responsable del almacén cuando un área solicita stock.'),
            ('SOLICITUD_STOCK_RESPUESTA', 'Respuesta del almacén',
             'Avisa a quien solicitó stock cuando el almacén acepta o rechaza la solicitud.'),
            ('DOTACION_INCOMPLETA', 'Dotación incompleta',
             'Avisa al responsable del almacén cuando al confirmar el día no alcanzó la existencia para el cupo.')
        ) AS t(codigo, nombre, descripcion)
        WHERE NOT EXISTS (SELECT 1 FROM notificacion_tipos n WHERE n.codigo = t.codigo AND n.deleted = false);

        -- Se activan para los roles que ya reciben avisos de stock bajo el mínimo.
        IF to_regclass('public.rol_notificacion_tipo') IS NOT NULL THEN
            INSERT INTO rol_notificacion_tipo (id, created_at, updated_at, enabled, deleted, role_id, tipo_id)
            SELECT gen_random_uuid(), now(), now(), true, false, a.role_id, nuevo.id
            FROM rol_notificacion_tipo a
            JOIN notificacion_tipos stock ON stock.id = a.tipo_id AND stock.codigo = 'STOCK_BAJO_MINIMO'
            JOIN notificacion_tipos nuevo ON nuevo.codigo IN ('SOLICITUD_STOCK_NUEVA', 'SOLICITUD_STOCK_RESPUESTA', 'DOTACION_INCOMPLETA')
                                          AND nuevo.deleted = false
            WHERE a.deleted = false
              AND NOT EXISTS (SELECT 1 FROM rol_notificacion_tipo x
                              WHERE x.role_id = a.role_id AND x.tipo_id = nuevo.id AND x.deleted = false);
        END IF;
    END IF;

    -- El CHECK de la columna tipo (si Hibernate lo generó) se rehace con los tipos nuevos.
    IF to_regclass('public.notificaciones') IS NOT NULL THEN
        FOR restriccion IN
            SELECT c.conname FROM pg_constraint c
            WHERE c.conrelid = 'public.notificaciones'::regclass AND c.contype = 'c'
              AND pg_get_constraintdef(c.oid) ILIKE '%tipo%' AND pg_get_constraintdef(c.oid) ILIKE '%STOCK_BAJO_MINIMO%'
        LOOP
            EXECUTE format('ALTER TABLE notificaciones DROP CONSTRAINT %I', restriccion.conname);
            habia_check := true;
        END LOOP;
        IF habia_check THEN
            ALTER TABLE notificaciones ADD CONSTRAINT notificaciones_tipo_check CHECK (tipo IN (
                'EQUIPO_MONTO_NO_CUBIERTO', 'META_VENTA_NO_CUBIERTA', 'STOCK_BAJO_MINIMO',
                'BOTELLON_ROTO_SOBRE_TOLERANCIA', 'ORDEN_ENVIADA_RECEPCION', 'VIGENCIA_EMPLEADO_POR_VENCER',
                'SOLICITUD_STOCK_NUEVA', 'SOLICITUD_STOCK_RESPUESTA', 'DOTACION_INCOMPLETA'));
        END IF;
    END IF;
END $$;

-- ---------------------------------------------------------------- preferencias por usuario (Buzón 7)
-- Valor JSON libre por clave (orden de tarjetas, modo oscuro, novedades vistas, favoritos…).
CREATE TABLE IF NOT EXISTS usuario_preferencias
(
    id          UUID         NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    enabled     BOOLEAN      NOT NULL DEFAULT true,
    deleted     BOOLEAN      NOT NULL DEFAULT false,
    empleado_id UUID         NOT NULL,
    clave       VARCHAR(150) NOT NULL,
    valor       TEXT,
    CONSTRAINT pk_usuario_preferencias PRIMARY KEY (id),
    CONSTRAINT fk_usuario_preferencias_empleado FOREIGN KEY (empleado_id) REFERENCES empleados (id),
    CONSTRAINT uk_usuario_preferencias UNIQUE (empleado_id, clave)
);
