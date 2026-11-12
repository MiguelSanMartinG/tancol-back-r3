-- Cantidad e importe por empleado / producto / día.
-- Importante: liquidacion_partidas debe unirse también por producto; si solo se une por
-- id_liquidacion, cada fila lte se multiplica por todas las partidas y SUM(lte.cantidad)
-- repite la cantidad del producto tantas veces como partidas haya en la liquidación.

SELECT
    DATE(l.fecha)         as fecha,
    e.id_empleado,
    concat(e.nombre, ' ', e.primer_apellido, ' ', e.segundo_apellido) as nombre,
    p.alias           as producto,
    p.nombre          as nombre_producto,
    SUM(lte.cantidad) as cantidad_vendida,
    SUM(COALESCE(lp_agg.importe_total, 0)) as importe
FROM liquidacion_totalizado_empleado lte
         left join liquidacion_totalizado lt ON lt.id = lte.origen_totalizado_id
         left join liquidacion l ON lt.origen_liquidacion_id_liquidacion = l.id_liquidacion
         left join empleados e ON e.id = lte.empleado_id
         left join productos_comerciales p ON lt.producto_comercial_id = p.id
         left join (
             select lp_sum.liquidacion_origen_id_liquidacion,
                    lp_sum.producto_comercial_id,
                    sum(lp_sum.importe) as importe_total
             from liquidacion_partidas lp_sum
             group by lp_sum.liquidacion_origen_id_liquidacion, lp_sum.producto_comercial_id
         ) lp_agg on lp_agg.liquidacion_origen_id_liquidacion = l.id_liquidacion
                 and lp_agg.producto_comercial_id = lt.producto_comercial_id
WHERE p.id IS NOT NULL AND  l.estados_liquidacion = 'FINALIZADA'
GROUP BY p.alias, e.correo, e.nombre, e.primer_apellido, e.segundo_apellido, e.id_empleado, DATE(l.fecha), p.nombre
ORDER BY e.id_empleado;


DROP FUNCTION IF EXISTS public.get_ventas_empleados_dia(uuid[], uuid[], timestamp without time zone, timestamp without time zone);
DROP FUNCTION IF EXISTS public.get_ventas_empleados_dia(uuid[], uuid[], uuid[], uuid[], boolean, timestamp without time zone, timestamp without time zone);

CREATE OR REPLACE FUNCTION public.get_ventas_empleados_dia(
    empleado_ids uuid[] DEFAULT NULL::uuid[],
    productos_ids uuid[] DEFAULT NULL::uuid[],
    p_ruta_ids uuid[] DEFAULT NULL::uuid[],
    p_conceptos_venta uuid[] DEFAULT NULL::uuid[],
    p_facturados boolean DEFAULT NULL,
    p_inicio_fecha timestamp DEFAULT NULL::timestamp without time zone,
    p_fin_fecha timestamp DEFAULT NULL::timestamp without time zone
)
    RETURNS TABLE
            (
                fecha            date,
                id_empleado      bigint,
                nombre           text,
                producto         varchar,
                cantidad_vendida numeric,
                importe          numeric
            )
as
$$
BEGIN
    RETURN QUERY
        SELECT
               DATE(l.fecha)         as fecha,
               e.id_empleado,
               concat(e.nombre, ' ', e.primer_apellido, ' ', e.segundo_apellido) as nombre,
               p.alias                                                           as producto,
               SUM(lte.cantidad)                                                 as cantidad_vendida,
               SUM(COALESCE(lp_agg.importe_total, 0))

        FROM liquidacion_totalizado_empleado lte
                 left join liquidacion_totalizado lt ON lt.id = lte.origen_totalizado_id
                 left join liquidacion l ON lt.origen_liquidacion_id_liquidacion = l.id_liquidacion
                 left join empleados e ON e.id = lte.empleado_id
                 left join productos_comerciales p ON lt.producto_comercial_id = p.id
                 left join (
                     select lp_sum.liquidacion_origen_id_liquidacion,
                            lp_sum.producto_comercial_id,
                            sum(lp_sum.importe) as importe_total
                     from liquidacion_partidas lp_sum
                     where COALESCE(lp_sum.cancelada, false) = false
                       and (p_conceptos_venta IS NULL OR lp_sum.conceptos_ventas_id = ANY (p_conceptos_venta))
                       and (p_facturados IS NULL OR lp_sum.factura = p_facturados)
                     group by lp_sum.liquidacion_origen_id_liquidacion, lp_sum.producto_comercial_id
                 ) lp_agg on lp_agg.liquidacion_origen_id_liquidacion = l.id_liquidacion
                         and lp_agg.producto_comercial_id = lt.producto_comercial_id

        WHERE
          -- Filtro por empleados (si es null, trae todos).
            (empleado_ids IS NULL OR e.id = ANY (empleado_ids))
          AND (productos_ids IS NULL OR p.id = ANY (productos_ids))
          AND (p_ruta_ids IS NULL OR l.ruta_id = ANY (p_ruta_ids))
          -- Filtro por rango de fechas.
          AND (p_inicio_fecha IS NULL OR l.fecha >= p_inicio_fecha)
          AND (p_fin_fecha IS NULL OR l.fecha <= p_fin_fecha)
          AND l.estados_liquidacion = 'FINALIZADA'
          AND (
                (p_conceptos_venta IS NULL AND p_facturados IS NULL)
                OR lp_agg.liquidacion_origen_id_liquidacion IS NOT NULL
              )
        GROUP BY p.alias, e.nombre, e.primer_apellido, e.segundo_apellido, e.id_empleado, DATE(l.fecha)
        ORDER BY DATE(l.fecha) DESC;
END;
$$ language plpgsql;

select * from get_ventas_empleados_dia(null,
                                   null,
                                   null,
                                   null,
                                   null,
                                   '2026-05-11T00:54:13.246Z',
                                   '2026-05-11T23:54:13.246Z');
