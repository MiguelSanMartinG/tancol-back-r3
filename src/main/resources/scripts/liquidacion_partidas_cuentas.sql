-- BORRAR
DROP FUNCTION IF EXISTS get_liquidaciones_partidas_cuentas(uuid[], uuid[], uuid[], timestamp, timestamp, boolean, boolean, boolean, varchar[], varchar);

-- SELECT
SELECT l.folio                                                        AS folio,
       l.bloque_nota                                                  AS bloque_nota,
       lp.folio                                                       AS folio_nota,
       r.nombre                                                       AS nombre_ruta,
       pc.alias                                                       AS nombre_producto,
       lp.cantidad                                                    AS cantidad,
       lp.precio                                                      AS precio,
       lp.descuento                                                   AS descuento,
       lp.importe                                                     AS importe,
       d.cuentaa                                                      AS cuentaA,
       d.cuentab                                                      AS cuentaB,
       c.nombre_comercial                                             AS nombre_cliente,
       lcv.name                                                       AS concepto_venta,
       lcv.is_exclude                                                 AS f1,
       lcv.descuento                                                  AS descuento,
       lcv.cancelado                                                  AS cancelados,
       lp.forma_pago                                                  AS tipo_pago,
       l.estados_liquidacion                                          AS estado_liquidacion,
       concat(e.id_empleado, ' - ', e.nombre, ' ', e.primer_apellido) AS vendedor_chofer,
       lte.empleados_no_chofer                                        AS ayudantes,
       l.fecha::timestamp without time zone                           AS fecha
FROM liquidacion_partidas lp
         LEFT JOIN liquidacion l ON lp.liquidacion_origen_id_liquidacion = l.id_liquidacion
         LEFT JOIN productos_comerciales pc ON lp.producto_comercial_id = pc.id
         LEFT JOIN clientes c ON lp.cliente_id = c.id
         LEFT JOIN rutas r ON r.id = l.ruta_id
         LEFT JOIN public.liquidacion_conceptos_venta lcv ON lp.conceptos_ventas_id = lcv.id
         LEFT JOIN (SELECT DISTINCT ON (origen_liquidacion_id_liquidacion) * FROM liquidacion_totalizado) lt
                   ON lt.origen_liquidacion_id_liquidacion = l.id_liquidacion
         LEFT JOIN (SELECT DISTINCT ON (origen_totalizado_id) * FROM liquidacion_totalizado_empleado WHERE fue_chofer= TRUE) le
                   ON lt.id = le.origen_totalizado_id
         LEFT JOIN empleados e ON le.empleado_id = e.id
         LEFT JOIN (SELECT le.origen_totalizado_id,
                           STRING_AGG(
                           e.id_empleado || ' - ' || e.nombre,
                           ', '
                           ORDER BY e.nombre) FILTER (WHERE le.fue_chofer = false)
                               AS empleados_no_chofer
                    FROM liquidacion_totalizado_empleado le
                             JOIN empleados e ON e.id = le.empleado_id
                    GROUP BY le.origen_totalizado_id) lte ON lte.origen_totalizado_id = lt.id
         LEFT JOIN (SELECT DISTINCT ON (liquidacion_partida_entity_id_partida) *
                    FROM liquidacion_distribucion_descuento) d
                   ON lp.id_partida = d.liquidacion_partida_entity_id_partida
WHERE l.estados_liquidacion = 'FINALIZADA';
-- CREAR PROCEDURE
-- CREATE
-- FUNCION
CREATE OR REPLACE FUNCTION public.get_liquidaciones_partidas_cuentas(
    p_clientes_ids uuid[] DEFAULT NULL::uuid[],
    p_ruta_ids uuid[] DEFAULT NULL::uuid[],
    p_productos_ids uuid[] DEFAULT NULL::uuid[],
    p_inicio_fecha timestamp DEFAULT NULL::timestamp without time zone,
    p_fin_fecha timestamp DEFAULT NULL::timestamp without time zone,
    p_f1 boolean DEFAULT NULL,
    p_descuento boolean DEFAULT NULL,
    p_cancelados boolean DEFAULT NULL,
    p_tipos_pago varchar[] DEFAULT NULL::varchar[],
    p_estado_liquidacion varchar DEFAULT NULL::varchar
)
    RETURNS TABLE
            (
                folio              varchar,
                bloque_nota        varchar,
                folio_nota         bigint,
                nombre_ruta        varchar,
                nombre_producto    varchar,
                cantidad           numeric,
                precio             numeric,
                descuento_real     numeric,
                importe            numeric,
                cuentaA            numeric,
                cuentaB            numeric,
                nombre_cliente     varchar,
                concepto_venta     varchar,
                f1                 boolean,
                descuento          boolean,
                cancelados         boolean,
                tipo_pago          varchar,
                estado_liquidacion varchar,
                vendedor_chofer    varchar,
                ayudantes          varchar,
                fecha              timestamp
            )
AS
$$
BEGIN
    RETURN QUERY
        SELECT l.folio                                                                 AS folio,
               l.bloque_nota                                                           AS bloque_nota,
               lp.folio                                                                AS folio_nota,
               r.nombre                                                                AS nombre_ruta,
               pc.alias                                                                AS nombre_producto,
               lp.cantidad                                                             AS cantidad,
               lp.precio                                                               AS precio,
               lp.descuento                                                            AS descuento_real,
               lp.importe                                                              AS importe,
               d.cuentaa                                                               AS cuentaA,
               d.cuentab                                                               AS cuentaB,
               c.nombre_comercial                                                      AS nombre_cliente,
               lcv.name                                                                AS concepto_venta,
               lcv.is_exclude                                                          AS f1,
               lcv.descuento                                                           AS descuento,
               lcv.cancelado                                                           AS cancelados,
               lp.forma_pago                                                           AS tipo_pago,
               l.estados_liquidacion                                                   AS estado_liquidacion,
               concat(e.id_empleado, ' - ', e.nombre, ' ', e.primer_apellido)::varchar AS vendedor_chofer,
               lte.empleados_no_chofer::varchar                                        AS ayudantes,
               l.fecha::timestamp without time zone                                    AS fecha

        FROM liquidacion_partidas lp
                 LEFT JOIN liquidacion l ON lp.liquidacion_origen_id_liquidacion = l.id_liquidacion
                 LEFT JOIN productos_comerciales pc ON lp.producto_comercial_id = pc.id
                 LEFT JOIN clientes c ON lp.cliente_id = c.id
                 LEFT JOIN rutas r ON r.id = l.ruta_id
                 LEFT JOIN public.liquidacion_conceptos_venta lcv ON lp.conceptos_ventas_id = lcv.id
                 LEFT JOIN (SELECT DISTINCT ON (origen_liquidacion_id_liquidacion) * FROM liquidacion_totalizado) lt
                           ON lt.origen_liquidacion_id_liquidacion = l.id_liquidacion
                 LEFT JOIN (SELECT DISTINCT ON (origen_totalizado_id) *
                            FROM liquidacion_totalizado_empleado
                            WHERE fue_chofer = TRUE
                            ORDER BY origen_totalizado_id, id) le ON lt.id = le.origen_totalizado_id
                 LEFT JOIN empleados e ON le.empleado_id = e.id
                 LEFT JOIN (SELECT le.origen_totalizado_id,
                                   STRING_AGG(
                                   e.id_empleado || ' - ' || e.nombre,
                                   ', '
                                   ORDER BY e.nombre
                                             ) FILTER (WHERE le.fue_chofer = false)
                                       AS empleados_no_chofer
                            FROM liquidacion_totalizado_empleado le
                                     JOIN empleados e ON e.id = le.empleado_id
                            GROUP BY le.origen_totalizado_id) lte ON lte.origen_totalizado_id = lt.id
                 LEFT JOIN liquidacion_distribucion_descuento d
                           ON lp.id_partida = d.liquidacion_partida_entity_id_partida
        WHERE
        COALESCE(lp.cancelada, false) = false
          AND COALESCE(l.estados_liquidacion, '') <> 'CANCELADA'
          AND l.estados_liquidacion = 'FINALIZADA'
          AND (p_clientes_ids IS NULL OR c.id = ANY (p_clientes_ids))
          -- Filtro por productos
          AND (p_productos_ids IS NULL OR lp.producto_comercial_id = ANY (p_productos_ids))
          -- Filtro por rutas
          AND (p_ruta_ids IS NULL OR r.id = ANY (p_ruta_ids))
          -- Filtro por rango de fechas
          AND (p_inicio_fecha IS NULL OR l.fecha >= p_inicio_fecha)
          AND (p_fin_fecha IS NULL OR l.fecha <= p_fin_fecha)
          -- Filtro por f1 (is_exclude)
          AND (p_f1 IS NULL OR lcv.is_exclude = p_f1)
          -- Filtro por descuento.
          AND (p_descuento IS NULL OR lcv.descuento = p_descuento)
          -- Filtro por cancelados
          AND (p_cancelados IS NULL OR lcv.cancelado = p_cancelados)
          -- Filtro por tipos de pago
          AND (p_tipos_pago IS NULL OR lp.forma_pago = ANY (p_tipos_pago))
          AND (p_estado_liquidacion IS NULL or l.estados_liquidacion = p_estado_liquidacion)
        ORDER BY l.fecha DESC;
END;
$$ LANGUAGE plpgsql;
-- TEST STORE
SELECT *
FROM get_liquidaciones_partidas_cuentas(
        NULL, NULL, NULL,                                    '2026-05-12T00:54:13.246Z',
        '2026-05-12T23:54:13.246Z',
        NULL, NULL, NULL, NULL, NULL);