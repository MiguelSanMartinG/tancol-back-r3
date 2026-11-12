DROP FUNCTION IF EXISTS public.get_reporte_total_corridas(
    timestamp without time zone,
    timestamp without time zone,
    character varying[],
    character varying[],
    uuid[],
    uuid[],
    boolean
);

CREATE OR REPLACE FUNCTION public.get_reporte_total_corridas(
    p_fecha_inicial timestamp without time zone,
    p_fecha_final timestamp without time zone,
    p_forma_pago character varying[] DEFAULT NULL::character varying[],
    p_tipo_ruta character varying[] DEFAULT NULL::character varying[],
    p_conceptos_venta_ids uuid[] DEFAULT NULL::uuid[],
    p_productos_ids uuid[] DEFAULT NULL::uuid[],
    p_factura boolean DEFAULT NULL
)
    RETURNS TABLE
            (
                nombre       character varying,
                id_producto  uuid,
                tipo_ruta    character varying,
                importe      numeric,
                id_concepto  uuid,
                concepto     character varying,
                empleado_id  uuid
            )
    LANGUAGE plpgsql
AS
$$
BEGIN
    RETURN QUERY
        SELECT producto_c.nombre,
               producto_c.id  AS id_producto,
               r.tipo_ruta,
               lp.importe,
               lcv.id         AS id_concepto,
               lcv.name       AS concepto,
               COALESCE(lte.empleado_id, chofer_liq.empleado_id) AS empleado_id
        FROM liquidacion l
                 INNER JOIN rutas r ON l.ruta_id = r.id
                 INNER JOIN public.liquidacion_partidas lp
                            ON l.id_liquidacion = lp.liquidacion_origen_id_liquidacion
                 INNER JOIN public.liquidacion_conceptos_venta lcv ON lp.conceptos_ventas_id = lcv.id
                 INNER JOIN public.productos_comerciales producto_c ON lp.producto_comercial_id = producto_c.id
                 LEFT JOIN public.liquidacion_totalizado lt
                            ON l.id_liquidacion = lt.origen_liquidacion_id_liquidacion
                           AND lt.producto_comercial_id = lp.producto_comercial_id
                 LEFT JOIN (
                     SELECT DISTINCT ON (lte.origen_totalizado_id)
                            lte.origen_totalizado_id,
                            lte.empleado_id
                     FROM public.liquidacion_totalizado_empleado lte
                     WHERE lte.fue_chofer = TRUE
                     ORDER BY lte.origen_totalizado_id, lte.id
                 ) lte ON lt.id = lte.origen_totalizado_id
                 LEFT JOIN (
                     SELECT DISTINCT ON (lt2.origen_liquidacion_id_liquidacion)
                            lt2.origen_liquidacion_id_liquidacion,
                            lte2.empleado_id
                     FROM public.liquidacion_totalizado lt2
                              INNER JOIN public.liquidacion_totalizado_empleado lte2
                                         ON lt2.id = lte2.origen_totalizado_id
                                        AND lte2.fue_chofer = TRUE
                     ORDER BY lt2.origen_liquidacion_id_liquidacion, lte2.id
                 ) chofer_liq ON chofer_liq.origen_liquidacion_id_liquidacion = l.id_liquidacion
        WHERE l.fecha BETWEEN p_fecha_inicial AND p_fecha_final
          AND l.estados_liquidacion = 'FINALIZADA'
          AND COALESCE(lp.cancelada, FALSE) = FALSE
          AND COALESCE(lte.empleado_id, chofer_liq.empleado_id) IS NOT NULL
          AND (p_forma_pago IS NULL OR lp.forma_pago = ANY (p_forma_pago))
          AND (p_tipo_ruta IS NULL OR r.tipo_ruta = ANY (p_tipo_ruta))
          AND (p_conceptos_venta_ids IS NULL OR lcv.id = ANY (p_conceptos_venta_ids))
          AND (p_productos_ids IS NULL OR producto_c.id = ANY (p_productos_ids))
          AND (p_factura IS NULL OR lp.factura = p_factura);
END;
$$;


-- SELECT PARA PRUEBAS A1

WITH
    e20 AS (
    SELECT x.empleado_id, sum(x.importe) as importe FROM
    get_reporte_total_corridas(
            '2026-05-01',
            '2026-05-30',
            ARRAY ['CONTADO'],
            null,
            ARRAY ['a6b9ecd8-aec2-4174-a353-f7896e276bfe']::uuid[],
            ARRAY ['a30911f5-b1d6-4dbc-aad2-1b0ba2d9d89a']::uuid[],
            false
         )x group by x.empleado_id
),
    des AS (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
        get_reporte_total_corridas(
            '2026-05-01',
            '2026-05-30',
            ARRAY ['VALE_CODIGO'],
            null,
            ARRAY ['8a0c4c7b-351b-4391-bbea-72ffbc1aa2c1','b6ccdf91-eb42-422a-b30c-bd0a028738fc']::uuid[],
            ARRAY ['8d798c8d-7982-4a8d-9d86-475d83c0c5a2']::uuid[],
            false
         ) x group by x.empleado_id
    ),
    pg AS (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    '2026-05-01',
                    '2026-05-30',
                    ARRAY ['CONTADO'],
                    null,
                    ARRAY ['8a0c4c7b-351b-4391-bbea-72ffbc1aa2c1','b6ccdf91-eb42-422a-b30c-bd0a028738fc']::uuid[],
                    ARRAY ['8d798c8d-7982-4a8d-9d86-475d83c0c5a2']::uuid[],
                    false
            ) x group by x.empleado_id
    ),
    bpsc AS (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    '2026-05-01',
                    '2026-05-30',
                    ARRAY ['VALE_CODIGO'],
                    null ,
                    ARRAY ['4c081941-cf3c-4d61-b74d-26745dd348e1']::uuid[],
                    ARRAY ['8d798c8d-7982-4a8d-9d86-475d83c0c5a2']::uuid[],
                    false
            ) x group by x.empleado_id
    ),
    bpsc2 AS (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    '2026-05-01',
                    '2026-05-30',
                    ARRAY ['CONTADO'],
                    ARRAY ['AUTO'] ,
                    ARRAY ['4c081941-cf3c-4d61-b74d-26745dd348e1']::uuid[],
                    ARRAY ['8d798c8d-7982-4a8d-9d86-475d83c0c5a2']::uuid[],
                    null
            ) x group by x.empleado_id
    ),
    exp AS (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    '2026-05-01',
                    '2026-05-30',
                    ARRAY ['CONTADO'],
                    ARRAY ['RP','RC'] ,
                    ARRAY ['3e397f5b-26f4-46b6-ab13-0f61b15108d0']::uuid[],
                    ARRAY ['8d798c8d-7982-4a8d-9d86-475d83c0c5a2']::uuid[],
                    false
            ) x group by x.empleado_id
    ),
    pg2 AS (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    '2026-05-01',
                    '2026-05-30',
                    ARRAY ['CONTADO'],
                    ARRAY ['AUTO'] ,
                    ARRAY ['b6ccdf91-eb42-422a-b30c-bd0a028738fc']::uuid[],
                    ARRAY ['8d798c8d-7982-4a8d-9d86-475d83c0c5a2','b5354c85-d520-4416-8ba0-274027c4fcf1']::uuid[],
                    null
            ) x group by x.empleado_id
    ),
    agua_purif as (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    '2026-05-01',
                    '2026-05-30',
                    ARRAY ['CONTADO'],
                    ARRAY ['AUTO_P1','AUTO'] ,
                    ARRAY ['a6b9ecd8-aec2-4174-a353-f7896e276bfe']::uuid[],
                    ARRAY ['5cec4d64-56cd-4c9f-89e8-2b5c408536f1']::uuid[],
                    false
            ) x group by x.empleado_id
    ),
    agua_pot as (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    '2026-05-01',
                    '2026-05-30',
                    ARRAY ['CONTADO'],
                    ARRAY ['AUTO_P4','AUTO'] ,
                    ARRAY ['a6b9ecd8-aec2-4174-a353-f7896e276bfe']::uuid[],
                    ARRAY ['4e3e0e2d-90af-48dc-b0e9-e960c927a6fa']::uuid[],
                    false
            ) x group by x.empleado_id
    ),
    agua_dm as (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    '2026-05-01',
                    '2026-05-30',
                    ARRAY ['CONTADO'],
                    ARRAY ['AUTO_PD','AUTO'] ,
                    ARRAY ['a6b9ecd8-aec2-4174-a353-f7896e276bfe']::uuid[],
                    ARRAY ['5cec4d64-56cd-4c9f-89e8-2b5c408536f1']::uuid[],
                    false
            ) x group by x.empleado_id
    ),
    h5kg as (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    '2026-05-01',
                    '2026-05-30',
                    ARRAY ['CONTADO'],
                    ARRAY ['AUTO'] ,
                    ARRAY ['8a0c4c7b-351b-4391-bbea-72ffbc1aa2c1','b6ccdf91-eb42-422a-b30c-bd0a028738fc']::uuid[],
                    ARRAY ['2a9cfca5-028f-4e4c-9b37-27982b0a24cd']::uuid[],
                    null
            ) x group by x.empleado_id
    ),
    h3kg as (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    '2026-05-01',
                    '2026-05-30',
                    ARRAY ['CONTADO'],
                    ARRAY ['AUTO'] ,
                    ARRAY ['8a0c4c7b-351b-4391-bbea-72ffbc1aa2c1','b6ccdf91-eb42-422a-b30c-bd0a028738fc']::uuid[],
                    ARRAY ['ee885255-f8db-4760-a486-8d35d25dadc9']::uuid[],
                    null
            ) x group by x.empleado_id
    ),

    com6 as (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    '2026-05-01',
                    '2026-05-30',
                    ARRAY ['CONTADO'],
                    ARRAY ['AUTO'] ,
                    ARRAY ['b6ccdf91-eb42-422a-b30c-bd0a028738fc']::uuid[],
                    ARRAY ['2a9cfca5-028f-4e4c-9b37-27982b0a24cd']::uuid[],
                    null
            ) x group by x.empleado_id
    ),
    hs as (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    '2026-05-01',
                    '2026-05-30',
                    ARRAY ['CONTADO'],
                    ARRAY ['AUTO'] ,
                    ARRAY ['b6ccdf91-eb42-422a-b30c-bd0a028738fc']::uuid[],
                    ARRAY ['ee885255-f8db-4760-a486-8d35d25dadc9']::uuid[],
                    null
            ) x group by x.empleado_id
    ),
    hb as (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    '2026-05-01',
                    '2026-05-30',
                    ARRAY ['CONTADO'],
                    ARRAY ['AUTO_P1'] ,
                    ARRAY ['b6ccdf91-eb42-422a-b30c-bd0a028738fc']::uuid[],
                    ARRAY ['2a9cfca5-028f-4e4c-9b37-27982b0a24cd']::uuid[],
                    null
            ) x group by x.empleado_id
    ),
    c350 as (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    '2026-05-01',
                    '2026-05-30',
                    ARRAY ['CONTADO'],
                    null ,
                    ARRAY ['b6ccdf91-eb42-422a-b30c-bd0a028738fc','8a0c4c7b-351b-4391-bbea-72ffbc1aa2c1']::uuid[],
                    ARRAY ['3364c9be-ca6f-4a22-b177-ef64b8ad21c0']::uuid[],
                    null
            ) x group by x.empleado_id
    ),
    c500 as (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    '2026-05-01',
                    '2026-05-30',
                    ARRAY ['CONTADO'],
                    null ,
                    ARRAY ['b6ccdf91-eb42-422a-b30c-bd0a028738fc','8a0c4c7b-351b-4391-bbea-72ffbc1aa2c1']::uuid[],
                    ARRAY ['c6396d35-dfbd-484f-bf1d-69bf1ab7d640']::uuid[],
                    null
            ) x group by x.empleado_id
    ),
    c1000 as (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    '2026-05-01',
                    '2026-05-30',
                    ARRAY ['CONTADO'],
                    null ,
                    ARRAY ['b6ccdf91-eb42-422a-b30c-bd0a028738fc','8a0c4c7b-351b-4391-bbea-72ffbc1aa2c1']::uuid[],
                    ARRAY ['1a7a47a9-aebe-4a6f-a077-7abe3ab09b2b']::uuid[],
                    null
            ) x group by x.empleado_id
    ),
    c1500 as (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    '2026-05-01',
                    '2026-05-30',
                    ARRAY ['CONTADO'],
                    null ,
                    ARRAY ['b6ccdf91-eb42-422a-b30c-bd0a028738fc','8a0c4c7b-351b-4391-bbea-72ffbc1aa2c1']::uuid[],
                    null,
                    null
            ) x group by x.empleado_id
    ),
    desc_hielo as (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    '2026-05-01',
                    '2026-05-30',
                    ARRAY ['CONTADO'],
                    ARRAY ['AUTO'] ,
                    ARRAY ['a6b9ecd8-aec2-4174-a353-f7896e276bfe']::uuid[],
                    ARRAY ['2a9cfca5-028f-4e4c-9b37-27982b0a24cd']::uuid[],
                    null
            ) x group by x.empleado_id
    ),
    desc_h5kg as (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    '2026-05-01',
                    '2026-05-30',
                    ARRAY ['CONTADO'],
                    ARRAY ['AUTO'] ,
                    ARRAY ['8a0c4c7b-351b-4391-bbea-72ffbc1aa2c1','b6ccdf91-eb42-422a-b30c-bd0a028738fc']::uuid[],
                    ARRAY ['2a9cfca5-028f-4e4c-9b37-27982b0a24cd']::uuid[],
                    null
            ) x group by x.empleado_id
    ),
    desc_h3kg as (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    '2026-05-01',
                    '2026-05-30',
                    ARRAY ['CONTADO'],
                    ARRAY ['AUTO'] ,
                    ARRAY ['8a0c4c7b-351b-4391-bbea-72ffbc1aa2c1','b6ccdf91-eb42-422a-b30c-bd0a028738fc']::uuid[],
                    ARRAY ['ee885255-f8db-4760-a486-8d35d25dadc9']::uuid[],
                    null
            ) x group by x.empleado_id
    ),
    pg3 as (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    '2026-05-01',
                    '2026-05-30',
                    ARRAY ['CONTADO'],
                    ARRAY ['AUTO_P1'] ,
                    ARRAY ['b6ccdf91-eb42-422a-b30c-bd0a028738fc']::uuid[],
                    ARRAY ['5cec4d64-56cd-4c9f-89e8-2b5c408536f1']::uuid[],
                    null
            ) x group by x.empleado_id
    ),
    e11 as (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    '2026-05-01',
                    '2026-05-30',
                    ARRAY ['CONTADO'],
                    ARRAY ['AUTO_P1'] ,
                    ARRAY ['a6b9ecd8-aec2-4174-a353-f7896e276bfe']::uuid[],
                    ARRAY ['5cec4d64-56cd-4c9f-89e8-2b5c408536f1']::uuid[],
                    null
            ) x group by x.empleado_id
    ),
    empleados AS (
        SELECT empleado_id FROM e20
        UNION
        SELECT empleado_id FROM des
        UNION
        SELECT empleado_id FROM pg
        UNION
        SELECT empleado_id FROM pg2
        UNION
        SELECT empleado_id FROM bpsc
        UNION
        SELECT empleado_id FROM bpsc2
        UNION
        SELECT empleado_id FROM exp
        UNION
        SELECT empleado_id FROM agua_purif
        UNION
        SELECT empleado_id FROM agua_pot
        UNION
        SELECT empleado_id FROM agua_dm
        UNION
        SELECT empleado_id FROM com6
        UNION
        SELECT empleado_id FROM hs
        UNION
        SELECT empleado_id FROM hb
        UNION
        SELECT empleado_id FROM c350
        UNION
        SELECT empleado_id FROM c500
        UNION
        SELECT empleado_id FROM c1000
        UNION
        SELECT empleado_id FROM c1500
        UNION
        SELECT empleado_id FROM desc_hielo
        UNION
        SELECT empleado_id FROM pg3
        UNION
        SELECT empleado_id FROM e11
    )
SELECT em.empleado_id,
       COALESCE(e.importe, 0)   AS e20,
       COALESCE(d.importe, 0)   AS des_liq20,
       COALESCE(p.importe, 0)   AS pg,
--        COALESCE(p2.importe, 0)  AS pg2,
--        COALESCE(bp2.importe, 0) AS bpsc2,
       COALESCE(bp.importe, 0)  AS bpsc,
       COALESCE(ex.importe, 0)  AS exp,
       COALESCE(ap.importe, 0)  AS agua_purif,
       COALESCE(apt.importe, 0) AS agua_pot,
       COALESCE(adm.importe, 0) AS agua_dm,

       COALESCE(c6.importe, 0)   AS com6,
       COALESCE(hs_t.importe, 0) AS hs,
       COALESCE(hb_t.importe, 0) AS hb,
       COALESCE(c350_t.importe, 0) AS c350,
       COALESCE(c500_t.importe, 0) AS c500,
       COALESCE(c1000_t.importe, 0) AS c1000,
       COALESCE(c1500_t.importe, 0) AS c1500,
       COALESCE(dh.importe, 0)   AS desc_h5kg,
--        COALESCE(p3.importe, 0)   AS pg3,
       COALESCE(e11_t.importe, 0) AS e11
FROM empleados em
         LEFT JOIN e20 e ON e.empleado_id = em.empleado_id
         LEFT JOIN des d ON d.empleado_id = em.empleado_id
         LEFT JOIN pg p ON p.empleado_id = em.empleado_id
         LEFT JOIN pg2 p2 ON p2.empleado_id = em.empleado_id
         LEFT JOIN bpsc bp ON bp.empleado_id = em.empleado_id
         LEFT JOIN bpsc2 bp2 ON bp2.empleado_id = em.empleado_id
         LEFT JOIN exp ex ON ex.empleado_id = em.empleado_id
         LEFT JOIN agua_purif ap ON ap.empleado_id = em.empleado_id
         LEFT JOIN agua_pot apt ON apt.empleado_id = em.empleado_id
         LEFT JOIN agua_dm adm ON adm.empleado_id = em.empleado_id
         LEFT JOIN com6 c6 ON c6.empleado_id = em.empleado_id
         LEFT JOIN hs hs_t ON hs_t.empleado_id = em.empleado_id
         LEFT JOIN hb hb_t ON hb_t.empleado_id = em.empleado_id
         LEFT JOIN c350 c350_t ON c350_t.empleado_id = em.empleado_id
         LEFT JOIN c500 c500_t ON c500_t.empleado_id = em.empleado_id
         LEFT JOIN c1000 c1000_t ON c1000_t.empleado_id = em.empleado_id
         LEFT JOIN c1500 c1500_t ON c1500_t.empleado_id = em.empleado_id
         LEFT JOIN desc_hielo dh ON dh.empleado_id = em.empleado_id
         LEFT JOIN pg3 p3 ON p3.empleado_id = em.empleado_id
         LEFT JOIN e11 e11_t ON e11_t.empleado_id = em.empleado_id
ORDER BY em.empleado_id;

-- Probar FUNCIÓN
SELECT *
FROM get_reporte_total_corridas(
        '2026-05-01',
        '2026-05-30',
        null,
        null,
        ARRAY ['b6ccdf91-eb42-422a-b30c-bd0a028738fc']::uuid[],
        ARRAY ['ae5c86dc-6ab1-48cc-a3e4-454bd720db63']::uuid[],
        null
     );

SELECT *
FROM get_reporte_total_corridas(
        '2026-05-01',
        '2026-05-30',
        NULL, NULL, NULL, NULL, NULL
     );

-- SELECT SINGULAR

SELECT producto_c.nombre,producto_c.id as id_producto,r.tipo_ruta, importe, lcv.id as id_concepto, lcv.name as concepto,empleado_id
-- SELECT *
FROM liquidacion l
         inner join rutas r on l.ruta_id = r.id
         inner join public.liquidacion_partidas lp on l.id_liquidacion = lp.liquidacion_origen_id_liquidacion
         inner join public.liquidacion_totalizado lt
                    on l.id_liquidacion = lt.origen_liquidacion_id_liquidacion
                   and lt.producto_comercial_id = lp.producto_comercial_id
         inner join public.liquidacion_totalizado_empleado lte on lt.id = lte.origen_totalizado_id
         inner join public.liquidacion_conceptos_venta lcv on lp.conceptos_ventas_id = lcv.id
         inner join public.productos_comerciales producto_c on lp.producto_comercial_id = producto_c.id
WHERE l.fecha BETWEEN '2026-05-14' AND '2026-05-30'
  AND l.estados_liquidacion = 'FINALIZADA'
  AND lp.forma_pago = ANY (ARRAY ['CONTADO'])
  AND r.tipo_ruta = ANY (ARRAY ['RP','RC'])
  AND lcv.id = ANY (ARRAY ['b6ccdf91-eb42-422a-b30c-bd0a028738fc']::uuid[])
  AND producto_c.id = ANY (ARRAY ['8d798c8d-7982-4a8d-9d86-475d83c0c5a2']::uuid[])
  AND lte.fue_chofer = true
  AND (factura IS NULL OR factura = false);