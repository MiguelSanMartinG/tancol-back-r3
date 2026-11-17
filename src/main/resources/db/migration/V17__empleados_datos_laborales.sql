-- Datos laborales del empleado: NSS, RFC, CURP, fechas de ingreso y egreso, observaciones,
-- tipo de contratación (PLANTA, TEMPORAL, PRACTICANTE) y fecha de vigencia (temporal y practicante).
ALTER TABLE empleados
    ADD COLUMN IF NOT EXISTS nss               VARCHAR(11),
    ADD COLUMN IF NOT EXISTS rfc               VARCHAR(13),
    ADD COLUMN IF NOT EXISTS curp              VARCHAR(18),
    ADD COLUMN IF NOT EXISTS fecha_ingreso     DATE,
    ADD COLUMN IF NOT EXISTS fecha_egreso      DATE,
    ADD COLUMN IF NOT EXISTS observaciones     VARCHAR(1000),
    ADD COLUMN IF NOT EXISTS tipo_contratacion VARCHAR(20),
    ADD COLUMN IF NOT EXISTS fecha_vigencia    DATE;

-- Tipo de notificación VIGENCIA_EMPLEADO_POR_VENCER: aviso 2 días antes de la fecha de vigencia.
-- Queda activado para los roles que hoy pueden leer el módulo Empleados; desde Configuración se
-- puede activar o quitar por rol. Las tablas de notificaciones se crearon fuera de Flyway, así que
-- todo se hace solo si existen (en una base nueva no falla).
DO $$
DECLARE
    restriccion RECORD;
    habia_check BOOLEAN := false;
BEGIN
    IF to_regclass('public.notificacion_tipos') IS NOT NULL THEN
        INSERT INTO notificacion_tipos (id, created_at, updated_at, enabled, deleted, codigo, nombre, descripcion)
        SELECT gen_random_uuid(), now(), now(), true, false,
               'VIGENCIA_EMPLEADO_POR_VENCER',
               'Vigencia de empleado por vencer',
               'Avisa 2 días antes de que termine la vigencia de un empleado temporal o practicante.'
        WHERE NOT EXISTS (
            SELECT 1 FROM notificacion_tipos
            WHERE codigo = 'VIGENCIA_EMPLEADO_POR_VENCER' AND deleted = false
        );

        IF to_regclass('public.rol_notificacion_tipo') IS NOT NULL THEN
            INSERT INTO rol_notificacion_tipo (id, created_at, updated_at, enabled, deleted, role_id, tipo_id)
            SELECT gen_random_uuid(), now(), now(), true, false, roles_empleados.role_id, t.id
            FROM (
                SELECT DISTINCT p.role_id
                FROM perfiles p
                JOIN modulos m ON m.id = p.module_id
                WHERE p.can_read = true
                  AND p.deleted = false
                  AND m.deleted = false
                  AND m.name = 'Empleados'
                  AND p.role_id IS NOT NULL
            ) roles_empleados
            JOIN notificacion_tipos t ON t.codigo = 'VIGENCIA_EMPLEADO_POR_VENCER' AND t.deleted = false
            WHERE NOT EXISTS (
                SELECT 1 FROM rol_notificacion_tipo a
                WHERE a.role_id = roles_empleados.role_id
                  AND a.tipo_id = t.id
                  AND a.deleted = false
            );
        END IF;
    END IF;

    -- Si la columna tipo tiene un CHECK con los valores del enum (Hibernate 6 lo genera así),
    -- se reemplaza por uno que incluya el tipo nuevo.
    IF to_regclass('public.notificaciones') IS NOT NULL THEN
        FOR restriccion IN
            SELECT c.conname
            FROM pg_constraint c
            WHERE c.conrelid = 'public.notificaciones'::regclass
              AND c.contype = 'c'
              AND pg_get_constraintdef(c.oid) ILIKE '%tipo%'
              AND pg_get_constraintdef(c.oid) ILIKE '%STOCK_BAJO_MINIMO%'
        LOOP
            EXECUTE format('ALTER TABLE notificaciones DROP CONSTRAINT %I', restriccion.conname);
            habia_check := true;
        END LOOP;

        IF habia_check THEN
            ALTER TABLE notificaciones ADD CONSTRAINT notificaciones_tipo_check CHECK (tipo IN (
                'EQUIPO_MONTO_NO_CUBIERTO',
                'META_VENTA_NO_CUBIERTA',
                'STOCK_BAJO_MINIMO',
                'BOTELLON_ROTO_SOBRE_TOLERANCIA',
                'ORDEN_ENVIADA_RECEPCION',
                'VIGENCIA_EMPLEADO_POR_VENCER'
            ));
        END IF;
    END IF;
END $$;
