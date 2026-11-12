-- Tipo de notificación ORDEN_ENVIADA_RECEPCION: avisa en la campana cuando una orden de compra
-- se manda a Recepción de compra. Los roles que lo reciben se asignan desde Configuración.
--
-- Las tablas de notificaciones no se crean en ninguna migración anterior (se crearon fuera de
-- Flyway), así que todo se hace solo si existen. En una base nueva esta migración no falla.
DO $$
DECLARE
    restriccion RECORD;
    habia_check BOOLEAN := false;
BEGIN
    IF to_regclass('public.notificacion_tipos') IS NOT NULL THEN
        INSERT INTO notificacion_tipos (id, created_at, updated_at, enabled, deleted, codigo, nombre, descripcion)
        SELECT gen_random_uuid(), now(), now(), true, false,
               'ORDEN_ENVIADA_RECEPCION',
               'Orden enviada a recepción',
               'Avisa cuando una orden de compra se manda a Recepción de compra para recibir el producto.'
        WHERE NOT EXISTS (
            SELECT 1 FROM notificacion_tipos
            WHERE codigo = 'ORDEN_ENVIADA_RECEPCION' AND deleted = false
        );
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

        -- Solo se vuelve a crear si existía: no se agrega una restricción que la base no tenía.
        IF habia_check THEN
            ALTER TABLE notificaciones ADD CONSTRAINT notificaciones_tipo_check CHECK (tipo IN (
                'EQUIPO_MONTO_NO_CUBIERTO',
                'META_VENTA_NO_CUBIERTA',
                'STOCK_BAJO_MINIMO',
                'BOTELLON_ROTO_SOBRE_TOLERANCIA',
                'ORDEN_ENVIADA_RECEPCION'
            ));
        END IF;
    END IF;
END $$;
