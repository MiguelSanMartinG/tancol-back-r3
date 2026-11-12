--  SCRIPT 2 --- VENTAS/EMPLEADO
SELECT
    e.id_empleado,
       concat(e.nombre, ' ', e.primer_apellido, ' ', e.segundo_apellido) as nombre,
       p.alias           as producto,
       SUM(lte.cantidad) as cantidad_vendida
FROM liquidacion_totalizado_empleado lte
         left join liquidacion_totalizado lt ON lt.id = lte.origen_totalizado_id
         left join liquidacion l ON lt.origen_liquidacion_id_liquidacion = l.id_liquidacion
         left join empleados e ON e.id = lte.empleado_id
         left join productos_comerciales p ON lt.producto_comercial_id = p.id
WHERE p.id IS NOT NULL AND  l.estados_liquidacion = 'FINALIZADA'
GROUP BY p.alias, e.correo, e.nombre, e.primer_apellido, e.segundo_apellido, e.id_empleado;

DROP FUNCTION IF EXISTS public.get_ventas_empleados(uuid[], uuid[], timestamp without time zone, timestamp without time zone);
DROP FUNCTION IF EXISTS public.get_ventas_empleados(uuid[], uuid[], uuid[], timestamp without time zone, timestamp without time zone);

CREATE OR REPLACE FUNCTION public.get_ventas_empleados(
    empleado_ids uuid[] DEFAULT NULL::uuid[],
    productos_ids uuid[] default null::uuid[],
    p_ruta_ids uuid[] DEFAULT NULL::uuid[],
    p_inicio_fecha timestamp DEFAULT NULL::timestamp without time zone,
    p_fin_fecha timestamp DEFAULT NULL::timestamp without time zone
)
    RETURNS TABLE
            (
                id_empleado      bigint,
                nombre           text,
                producto         varchar,
                cantidad_vendida numeric
            )
as
$$
BEGIN
    RETURN QUERY
        SELECT e.id_empleado,
               concat(e.nombre, ' ', e.primer_apellido, ' ', e.segundo_apellido) as nombre,
               p.alias                                                           as producto,
               SUM(lte.cantidad)                                                 as cantidad_vendida
        FROM liquidacion_totalizado_empleado lte
                 left join liquidacion_totalizado lt ON lt.id = lte.origen_totalizado_id
                 left join liquidacion l ON lt.origen_liquidacion_id_liquidacion = l.id_liquidacion
                 left join empleados e ON e.id = lte.empleado_id
                 left join productos_comerciales p ON lt.producto_comercial_id = p.id
        WHERE
          -- Filtro por clientes (si es null, trae todos).
            (empleado_ids IS NULL OR e.id = ANY (empleado_ids))
          AND (productos_ids IS NULL OR p.id = ANY (productos_ids))
          AND (p_ruta_ids IS NULL OR l.ruta_id = ANY (p_ruta_ids))
          -- Filtro por rango de fechas.
          AND (p_inicio_fecha IS NULL OR l.fecha >= p_inicio_fecha)
          AND (p_fin_fecha IS NULL OR l.fecha <= p_fin_fecha)
          AND  l.estados_liquidacion = 'FINALIZADA'
        GROUP BY p.alias, e.nombre, e.primer_apellido, e.segundo_apellido, e.id_empleado
        ORDER BY e.id_empleado DESC;
END;
$$ language plpgsql;

select * from get_ventas_empleados(null,
                                   null,
                                   null,
                                   '2025-07-08T21:54:13.246Z',
                                   '2026-12-08T21:54:13.246Z');

