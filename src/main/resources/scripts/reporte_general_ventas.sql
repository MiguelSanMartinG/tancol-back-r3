-- Reporte unificado: E20 (CONTADO + producto) y PG (concepto venta) por empleado_id.
-- Ajusta fechas y UUIDs según necesidad.

WITH
    liquidaciones_finalizadas AS (
        SELECT * FROM get_liquidaciones_finalizadas('2026-05-14', '2026-05-30')
    ),
    choferes AS (
        SELECT * FROM get_totalizados_choferes_por_fecha('2026-05-14', '2026-05-30')
    ),
    choferes_por_liquidacion AS (
        SELECT DISTINCT empleado_id, origen_liquidacion_id_liquidacion
        FROM choferes
    ),
    -- E20: producto fijo → agregar partidas solo por liquidación, atribuir a choferes del mismo producto
    partidas_e20 AS (
        SELECT lp.liquidacion_origen_id_liquidacion,
               SUM(lp.importe) AS importe_total
        FROM liquidacion_partidas lp
                 INNER JOIN liquidaciones_finalizadas l
                            ON lp.liquidacion_origen_id_liquidacion = l.id_liquidacion
                 INNER JOIN rutas r
                            ON r.id = l.ruta_id
                                AND r.tipo_ruta IS NOT NULL
        WHERE lp.forma_pago = 'CONTADO'
          AND lp.producto_comercial_id = 'ae5c86dc-6ab1-48cc-a3e4-454bd720db63'
          AND lp.factura = false
        GROUP BY lp.liquidacion_origen_id_liquidacion
    ),
    agg_e20 AS (
        SELECT c.empleado_id,
               SUM(COALESCE(p.importe_total, 0)) AS e20
        FROM choferes c
                 LEFT JOIN partidas_e20 p
                           ON p.liquidacion_origen_id_liquidacion = c.origen_liquidacion_id_liquidacion
        WHERE c.producto_comercial_id = 'ae5c86dc-6ab1-48cc-a3e4-454bd720db63'
        GROUP BY c.empleado_id
    ),
    partidas_pg AS (
        SELECT lp.liquidacion_origen_id_liquidacion,
               lp.producto_comercial_id,
               SUM(lp.importe) AS importe_total
        FROM liquidacion_partidas lp
                 INNER JOIN liquidaciones_finalizadas l
                            ON lp.liquidacion_origen_id_liquidacion = l.id_liquidacion
                 INNER JOIN rutas r
                            ON r.id = l.ruta_id
                                AND r.tipo_ruta = 'RP'
        WHERE lp.conceptos_ventas_id = 'b6ccdf91-eb42-422a-b30c-bd0a028738fc'
        GROUP BY lp.liquidacion_origen_id_liquidacion, lp.producto_comercial_id
    ),
    agg_pg AS (
        SELECT c.empleado_id,
               SUM(p.importe_total) AS pg
        FROM choferes c
                 INNER JOIN partidas_pg p
                            ON p.liquidacion_origen_id_liquidacion = c.origen_liquidacion_id_liquidacion
                                AND p.producto_comercial_id = c.producto_comercial_id
        GROUP BY c.empleado_id
    ),
    partidas_exp AS (
        SELECT lp.liquidacion_origen_id_liquidacion,
               lp.producto_comercial_id,
               SUM(lp.importe) AS importe_total
        FROM liquidacion_partidas lp
                 INNER JOIN liquidaciones_finalizadas l
                            ON lp.liquidacion_origen_id_liquidacion = l.id_liquidacion
                 INNER JOIN rutas r
                            ON r.id = l.ruta_id
                                AND r.tipo_ruta = ''
        WHERE lp.conceptos_ventas_id = '3e397f5b-26f4-46b6-ab13-0f61b15108d0'
        GROUP BY lp.liquidacion_origen_id_liquidacion, lp.producto_comercial_id
    ),
    agg_exp AS (
        SELECT c.empleado_id,
               SUM(p.importe_total) AS exp
        FROM choferes c
                 INNER JOIN liquidaciones_finalizadas l
                            ON c.origen_liquidacion_id_liquidacion = l.id_liquidacion
                 INNER JOIN rutas r
                            ON r.id = l.ruta_id
                                AND r.tipo_ruta IN ('RP', 'RC')
                 INNER JOIN partidas_exp p
                            ON p.liquidacion_origen_id_liquidacion = l.id_liquidacion
                                AND p.producto_comercial_id = c.producto_comercial_id
        GROUP BY c.empleado_id
    ),
    partidas_desc AS (
        SELECT lp.liquidacion_origen_id_liquidacion,
               SUM(lp.importe) AS importe_total
        FROM liquidacion_partidas lp
                 INNER JOIN liquidaciones_finalizadas l
                            ON lp.liquidacion_origen_id_liquidacion = l.id_liquidacion
        WHERE lp.forma_pago = 'VALE_CODIGO'
        GROUP BY lp.liquidacion_origen_id_liquidacion
    ),
    agg_desc AS (
        SELECT c.empleado_id,
               SUM(COALESCE(p.importe_total, 0)) AS des
        FROM choferes_por_liquidacion c
                 LEFT JOIN partidas_desc p
                           ON p.liquidacion_origen_id_liquidacion = c.origen_liquidacion_id_liquidacion
        GROUP BY c.empleado_id
    ),
    partidas_bpsc AS (
        SELECT lp.liquidacion_origen_id_liquidacion,
               SUM(lp.importe) AS importe_total
        FROM liquidacion_partidas lp
                 INNER JOIN liquidaciones_finalizadas l
                            ON lp.liquidacion_origen_id_liquidacion = l.id_liquidacion
                 INNER JOIN rutas r
                            ON r.id = l.ruta_id
                                AND r.tipo_ruta IN ('RP', 'RC')
        WHERE lp.conceptos_ventas_id = '4c081941-cf3c-4d61-b74d-26745dd348e1'
        GROUP BY lp.liquidacion_origen_id_liquidacion
    ),
    agg_bpsc AS (
        SELECT c.empleado_id,
               SUM(COALESCE(p.importe_total, 0)) AS bpsc
        FROM choferes_por_liquidacion c
                 LEFT JOIN partidas_bpsc p
                           ON p.liquidacion_origen_id_liquidacion = c.origen_liquidacion_id_liquidacion
        GROUP BY c.empleado_id
    ),
    empleados AS (
        SELECT empleado_id FROM agg_e20
        UNION
        SELECT empleado_id FROM agg_pg
        UNION
        SELECT empleado_id FROM agg_exp
        UNION
        SELECT empleado_id FROM agg_desc
        UNION
        SELECT empleado_id FROM agg_bpsc
    )
SELECT em.empleado_id,
       COALESCE(e.e20, 0) AS e20,
       COALESCE(p.pg, 0) AS pg,
       COALESCE(ex.exp, 0) AS exp,
       COALESCE(des.des, 0) AS des,
       COALESCE(b.bpsc, 0) AS bpsc,
       COALESCE(b.bpsc, 0) AS bpsc2
FROM empleados em
         LEFT JOIN agg_e20 e ON e.empleado_id = em.empleado_id
         LEFT JOIN agg_pg p ON p.empleado_id = em.empleado_id
         LEFT JOIN agg_exp ex ON ex.empleado_id = em.empleado_id
         LEFT JOIN agg_desc des ON des.empleado_id = em.empleado_id
         LEFT JOIN agg_bpsc b ON b.empleado_id = em.empleado_id
ORDER BY em.empleado_id;
