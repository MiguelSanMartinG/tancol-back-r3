-- CON FACTURA

WITH
    e20 AS (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    :fechaInicio,
                    :fechaFin,
                    ARRAY ['CONTADO'],
                    ARRAY ['RP','RC'],
                    ARRAY ['b6ccdf91-eb42-422a-b30c-bd0a028738fc']::uuid[],
                    ARRAY ['ae5c86dc-6ab1-48cc-a3e4-454bd720db63']::uuid[],
                    true
            ) x group by x.empleado_id
    ),
    desc_liq AS (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    :fechaInicio,
                    :fechaFin,
                    ARRAY ['VALE_CODIGO'],
                    null,
                    ARRAY ['8a0c4c7b-351b-4391-bbea-72ffbc1aa2c1','b6ccdf91-eb42-422a-b30c-bd0a028738fc']::uuid[],
                    ARRAY ['8d798c8d-7982-4a8d-9d86-475d83c0c5a2']::uuid[],
                    true
            ) x group by x.empleado_id
    ),
    pg AS (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    :fechaInicio,
                    :fechaFin,
                    ARRAY ['CONTADO'],
                    null,
                    ARRAY ['8a0c4c7b-351b-4391-bbea-72ffbc1aa2c1','b6ccdf91-eb42-422a-b30c-bd0a028738fc']::uuid[],
                    ARRAY ['8d798c8d-7982-4a8d-9d86-475d83c0c5a2']::uuid[],
                    true
            ) x group by x.empleado_id
    ),
    bpsc AS (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    :fechaInicio,
                    :fechaFin,
                    ARRAY ['VALE_CODIGO'],
                    null,
                    ARRAY ['4c081941-cf3c-4d61-b74d-26745dd348e1']::uuid[],
                    ARRAY ['8d798c8d-7982-4a8d-9d86-475d83c0c5a2']::uuid[],
                    true
            ) x group by x.empleado_id
    ),
    exp AS (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    :fechaInicio,
                    :fechaFin,
                    ARRAY ['CONTADO'],
                    ARRAY ['RP','RC'],
                    ARRAY ['3e397f5b-26f4-46b6-ab13-0f61b15108d0']::uuid[],
                    ARRAY ['8d798c8d-7982-4a8d-9d86-475d83c0c5a2']::uuid[],
                    true
            ) x group by x.empleado_id
    ),
    agua_purif AS (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    :fechaInicio,
                    :fechaFin,
                    ARRAY ['CONTADO'],
                    ARRAY ['AUTO_P1','AUTO'],
                    ARRAY ['b6ccdf91-eb42-422a-b30c-bd0a028738fc']::uuid[],
                    ARRAY ['5cec4d64-56cd-4c9f-89e8-2b5c408536f1']::uuid[],
                    true
            ) x group by x.empleado_id
    ),
    agua_pot AS (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    :fechaInicio,
                    :fechaFin,
                    ARRAY ['CONTADO'],
                    ARRAY ['AUTO_P4','AUTO'],
                    ARRAY ['b6ccdf91-eb42-422a-b30c-bd0a028738fc']::uuid[],
                    ARRAY ['4e3e0e2d-90af-48dc-b0e9-e960c927a6fa']::uuid[],
                    true
            ) x group by x.empleado_id
    ),
    agua_dm AS (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    :fechaInicio,
                    :fechaFin,
                    ARRAY ['CONTADO'],
                    ARRAY ['AUTO_PD','AUTO'],
                    ARRAY ['b6ccdf91-eb42-422a-b30c-bd0a028738fc']::uuid[],
                    ARRAY ['097e6a59-a6ab-44ad-84f2-16ce972ced7c']::uuid[],
                    true
            ) x group by x.empleado_id
    ),
    h5kg AS (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    :fechaInicio,
                    :fechaFin,
                    ARRAY ['CONTADO'],
                    ARRAY ['RH'],
                    ARRAY ['8a0c4c7b-351b-4391-bbea-72ffbc1aa2c1','b6ccdf91-eb42-422a-b30c-bd0a028738fc']::uuid[],
                    ARRAY ['2a9cfca5-028f-4e4c-9b37-27982b0a24cd']::uuid[],
                    true
            ) x group by x.empleado_id
    ),
    h3kg AS (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    :fechaInicio,
                    :fechaFin,
                    ARRAY ['CONTADO'],
                    ARRAY ['RH'],
                    ARRAY ['8a0c4c7b-351b-4391-bbea-72ffbc1aa2c1','b6ccdf91-eb42-422a-b30c-bd0a028738fc']::uuid[],
                    ARRAY ['ee885255-f8db-4760-a486-8d35d25dadc9']::uuid[],
                    true
            ) x group by x.empleado_id
    ),
    c350 AS (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    :fechaInicio,
                    :fechaFin,
                    ARRAY ['CONTADO'],
                    null,
                    ARRAY ['b6ccdf91-eb42-422a-b30c-bd0a028738fc','8a0c4c7b-351b-4391-bbea-72ffbc1aa2c1']::uuid[],
                    ARRAY ['3364c9be-ca6f-4a22-b177-ef64b8ad21c0']::uuid[],
                    true
            ) x group by x.empleado_id
    ),
    c500 AS (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    :fechaInicio,
                    :fechaFin,
                    ARRAY ['CONTADO'],
                    null,
                    ARRAY ['b6ccdf91-eb42-422a-b30c-bd0a028738fc','8a0c4c7b-351b-4391-bbea-72ffbc1aa2c1']::uuid[],
                    ARRAY ['c6396d35-dfbd-484f-bf1d-69bf1ab7d640']::uuid[],
                    true
            ) x group by x.empleado_id
    ),
    c1000 AS (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    :fechaInicio,
                    :fechaFin,
                    ARRAY ['CONTADO'],
                    null,
                    ARRAY ['b6ccdf91-eb42-422a-b30c-bd0a028738fc','8a0c4c7b-351b-4391-bbea-72ffbc1aa2c1']::uuid[],
                    ARRAY ['1a7a47a9-aebe-4a6f-a077-7abe3ab09b2b']::uuid[],
                    true
            ) x group by x.empleado_id
    ),
    c1500 AS (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    :fechaInicio,
                    :fechaFin,
                    ARRAY ['CONTADO'],
                    null,
                    ARRAY ['b6ccdf91-eb42-422a-b30c-bd0a028738fc']::uuid[],
                    ARRAY ['ce8ed754-e5da-4ab8-a849-caf2873cbd67']::uuid[],
                    true
            ) x group by x.empleado_id
    ),
    desc_hielo AS (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    :fechaInicio,
                    :fechaFin,
                    ARRAY ['CONTADO'],
                    ARRAY ['AUTO'],
                    ARRAY ['a6b9ecd8-aec2-4174-a353-f7896e276bfe']::uuid[],
                    ARRAY ['2a9cfca5-028f-4e4c-9b37-27982b0a24cd']::uuid[],
                    true
            ) x group by x.empleado_id
    ),
    env11 AS (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    :fechaInicio,
                    :fechaFin,
                    ARRAY ['CONTADO'],
                    ARRAY ['AUTO_P1'],
                    ARRAY ['a6b9ecd8-aec2-4174-a353-f7896e276bfe']::uuid[],
                    ARRAY ['5cec4d64-56cd-4c9f-89e8-2b5c408536f1']::uuid[],
                    true
            ) x group by x.empleado_id
    ),
    liq11 AS (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    :fechaInicio,
                    :fechaFin,
                    ARRAY ['CONTADO'],
                    ARRAY ['AUTO_P1'],
                    ARRAY ['a6b9ecd8-aec2-4174-a353-f7896e276bfe']::uuid[],
                    ARRAY ['5cec4d64-56cd-4c9f-89e8-2b5c408536f1']::uuid[],
                    true
            ) x group by x.empleado_id
    ),
    env10 AS (
        SELECT x.empleado_id, sum(x.importe) as importe FROM
            get_reporte_total_corridas(
                    :fechaInicio,
                    :fechaFin,
                    ARRAY ['CONTADO'],
                    ARRAY ['AUTO_P1'],
                    ARRAY ['a6b9ecd8-aec2-4174-a353-f7896e276bfe']::uuid[],
                    ARRAY ['5cec4d64-56cd-4c9f-89e8-2b5c408536f1']::uuid[],
                    true
            ) x group by x.empleado_id
    ),
    empleados AS (
        SELECT empleado_id FROM e20
        UNION
        SELECT empleado_id FROM desc_liq
        UNION
        SELECT empleado_id FROM pg
        UNION
        SELECT empleado_id FROM bpsc
        UNION
        SELECT empleado_id FROM exp
        UNION
        SELECT empleado_id FROM agua_purif
        UNION
        SELECT empleado_id FROM agua_pot
        UNION
        SELECT empleado_id FROM agua_dm
        UNION
        SELECT empleado_id FROM h5kg
        UNION
        SELECT empleado_id FROM h3kg
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
        SELECT empleado_id FROM env11
        UNION
        SELECT empleado_id FROM liq11
        UNION
        SELECT empleado_id FROM env10
    )
SELECT em.empleado_id,
       COALESCE(e.importe, 0)       AS e20,
       COALESCE(dl.importe, 0)      AS desc_liq,
       COALESCE(p.importe, 0)       AS pg,
       COALESCE(bp.importe, 0)      AS bpsc,
       COALESCE(ex.importe, 0)      AS exp,
       COALESCE(ap.importe, 0)      AS agua_purif,
       COALESCE(apt.importe, 0)     AS agua_pot,
       COALESCE(adm.importe, 0)     AS agua_dm,
       COALESCE(h5.importe, 0)      AS h5kg,
       COALESCE(h3.importe, 0)      AS h3kg,
       COALESCE(c350_t.importe, 0)  AS c350,
       COALESCE(c500_t.importe, 0) AS c500,
       COALESCE(c1000_t.importe, 0) AS c1000,
       COALESCE(c1500_t.importe, 0) AS c1500,
       COALESCE(dhi.importe, 0)     AS desc_hielo,
       COALESCE(ev11.importe, 0)    AS env11,
       COALESCE(l11.importe, 0)     AS liq11,
       COALESCE(ev10.importe, 0)    AS env10
FROM empleados em
         LEFT JOIN e20 e ON e.empleado_id = em.empleado_id
         LEFT JOIN desc_liq dl ON dl.empleado_id = em.empleado_id
         LEFT JOIN pg p ON p.empleado_id = em.empleado_id
         LEFT JOIN bpsc bp ON bp.empleado_id = em.empleado_id
         LEFT JOIN exp ex ON ex.empleado_id = em.empleado_id
         LEFT JOIN agua_purif ap ON ap.empleado_id = em.empleado_id
         LEFT JOIN agua_pot apt ON apt.empleado_id = em.empleado_id
         LEFT JOIN agua_dm adm ON adm.empleado_id = em.empleado_id
         LEFT JOIN h5kg h5 ON h5.empleado_id = em.empleado_id
         LEFT JOIN h3kg h3 ON h3.empleado_id = em.empleado_id
         LEFT JOIN c350 c350_t ON c350_t.empleado_id = em.empleado_id
         LEFT JOIN c500 c500_t ON c500_t.empleado_id = em.empleado_id
         LEFT JOIN c1000 c1000_t ON c1000_t.empleado_id = em.empleado_id
         LEFT JOIN c1500 c1500_t ON c1500_t.empleado_id = em.empleado_id
         LEFT JOIN desc_hielo dhi ON dhi.empleado_id = em.empleado_id
         LEFT JOIN env11 ev11 ON ev11.empleado_id = em.empleado_id
         LEFT JOIN liq11 l11 ON l11.empleado_id = em.empleado_id
         LEFT JOIN env10 ev10 ON ev10.empleado_id = em.empleado_id
ORDER BY em.empleado_id;
