-- Tipos de notificación y su asignación por rol. Estas tablas se crearon fuera de Flyway en otras
-- bases, pero en producción no existían: las consultas fallaban y la pantalla de permisos del rol y
-- el envío de notificaciones no funcionaban. Todo es idempotente (IF NOT EXISTS / NOT EXISTS).

CREATE TABLE IF NOT EXISTS notificacion_tipos
(
    id          UUID         NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    enabled     BOOLEAN      NOT NULL DEFAULT true,
    deleted     BOOLEAN      NOT NULL DEFAULT false,
    codigo      VARCHAR(100) NOT NULL,
    nombre      VARCHAR(255) NOT NULL,
    descripcion VARCHAR(2000),
    CONSTRAINT pk_notificacion_tipos PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS rol_notificacion_tipo
(
    id         UUID    NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    enabled    BOOLEAN NOT NULL DEFAULT true,
    deleted    BOOLEAN NOT NULL DEFAULT false,
    role_id    BIGINT  NOT NULL,
    tipo_id    UUID    NOT NULL,
    CONSTRAINT pk_rol_notificacion_tipo PRIMARY KEY (id),
    CONSTRAINT fk_rol_notificacion_tipo_rol FOREIGN KEY (role_id) REFERENCES roles (id),
    CONSTRAINT fk_rol_notificacion_tipo_tipo FOREIGN KEY (tipo_id) REFERENCES notificacion_tipos (id)
);

CREATE INDEX IF NOT EXISTS idx_rol_notificacion_tipo_role ON rol_notificacion_tipo (role_id);

-- Catálogo de tipos (uno por valor del enum TipoNotificacion).
INSERT INTO notificacion_tipos (id, created_at, updated_at, enabled, deleted, codigo, nombre, descripcion)
SELECT gen_random_uuid(), now(), now(), true, false, t.codigo, t.nombre, t.descripcion
FROM (VALUES
    ('EQUIPO_MONTO_NO_CUBIERTO', 'Equipo con monto no cubierto',
     'Avisa cuando un equipo asignado a un cliente no cubre el monto de consumo esperado.'),
    ('META_VENTA_NO_CUBIERTA', 'Meta de venta no cubierta',
     'Avisa cuando no se alcanza una meta de venta en su periodo.'),
    ('STOCK_BAJO_MINIMO', 'Stock bajo el mínimo',
     'Avisa cuando la existencia de un producto queda por debajo de su stock mínimo.'),
    ('BOTELLON_ROTO_SOBRE_TOLERANCIA', 'Botellón roto sobre la tolerancia',
     'Avisa cuando los botellones rotos superan la tolerancia permitida.'),
    ('ORDEN_ENVIADA_RECEPCION', 'Orden enviada a recepción',
     'Avisa cuando una orden de compra se manda a Recepción de compra para recibir el producto.'),
    ('VIGENCIA_EMPLEADO_POR_VENCER', 'Vigencia de empleado por vencer',
     'Avisa 2 días antes de que termine la vigencia de un empleado temporal o practicante.')
) AS t(codigo, nombre, descripcion)
WHERE NOT EXISTS (SELECT 1 FROM notificacion_tipos n WHERE n.codigo = t.codigo AND n.deleted = false);

-- Asignación inicial: cada tipo a los roles que pueden leer el módulo Notificaciones (sin ese
-- permiso el rol no recibe avisos de todos modos). Se ajusta por rol en Configuración › Roles ›
-- Asignar permisos. Solo se hace si el rol aún no tiene ninguna asignación.
INSERT INTO rol_notificacion_tipo (id, created_at, updated_at, enabled, deleted, role_id, tipo_id)
SELECT gen_random_uuid(), now(), now(), true, false, r.role_id, t.id
FROM (
    SELECT DISTINCT p.role_id
    FROM perfiles p
    JOIN modulos m ON m.id = p.module_id
    WHERE p.can_read = true
      AND p.deleted = false
      AND m.deleted = false
      AND m.name = 'Notificaciones'
      AND p.role_id IS NOT NULL
) r
CROSS JOIN notificacion_tipos t
WHERE t.deleted = false
  AND NOT EXISTS (SELECT 1 FROM rol_notificacion_tipo a WHERE a.role_id = r.role_id);
