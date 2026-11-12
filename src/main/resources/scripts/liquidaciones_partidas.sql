-- BORRAR
DROP FUNCTION IF EXISTS get_liquidaciones_partidas(uuid[], uuid[], uuid[], timestamp, timestamp, uuid[], boolean, varchar[], varchar, varchar);


CREATE OR REPLACE FUNCTION get_liquidaciones_partidas(
    p_clientes_ids uuid[] DEFAULT NULL::uuid[],
    p_ruta_ids uuid[] DEFAULT NULL::uuid[],
    p_productos_ids uuid[] DEFAULT NULL::uuid[],
    p_inicio_fecha timestamp DEFAULT NULL::timestamp without time zone,
    p_fin_fecha timestamp DEFAULT NULL::timestamp without time zone,
    p_conceptos_venta uuid[] DEFAULT NULL::uuid[],
    p_facturados boolean DEFAULT NULL,
    p_tipos_pago varchar[] DEFAULT NULL::varchar[],
    p_estado_liquidacion varchar DEFAULT NULL::varchar,
    p_x varchar DEFAULT NULL::varchar
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
                cuentaa            numeric,
                cuentab            numeric,
                nombre_cliente     varchar,
                concepto_venta     varchar,
                f1                 boolean,
                descuento          boolean,
                cancelados         boolean,
                facturados         boolean,
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
        SELECT l.folio                               AS folio,
               l.bloque_nota                         AS bloque_nota,
               lp.folio                              AS folio_nota,
               r.nombre                              AS nombre_ruta,
               pc.alias                              AS nombre_producto,
               lp.cantidad                           AS cantidad,
               lp.precio                             AS precio,
               lp.descuento                          AS descuento_real,
               lp.importe                            AS importe,
               CASE WHEN p_x = 'xyz' THEN d.cuentaa ELSE NULL END AS cuentaa,
               CASE WHEN p_x = 'xyz' THEN d.cuentab ELSE NULL END AS cuentab,
               c.nombre_comercial                    AS nombre_cliente,
               lcv.name                              AS concepto_venta,
               lcv.is_exclude                        AS f1,
               lcv.descuento                         AS descuento,
               lcv.cancelado                         AS cancelados,
               lp.factura                            AS facturados,
               lp.forma_pago                         AS tipo_pago,
               l.estados_liquidacion                 AS estado_liquidacion,
               emp.chofer::varchar                   AS vendedor_chofer,
               emp.ayudantes::varchar                AS ayudantes,
               l.fecha::timestamp without time zone  AS fecha

        FROM liquidacion_partidas lp
                 LEFT JOIN liquidacion l
                           ON lp.liquidacion_origen_id_liquidacion = l.id_liquidacion
                 LEFT JOIN productos_comerciales pc
                           ON lp.producto_comercial_id = pc.id
                 LEFT JOIN clientes c
                           ON lp.cliente_id = c.id
                 LEFT JOIN rutas r
                           ON r.id = l.ruta_id
                 LEFT JOIN liquidacion_conceptos_venta lcv
                           ON lp.conceptos_ventas_id = lcv.id
                 LEFT JOIN (
            SELECT DISTINCT ON (origen_liquidacion_id_liquidacion) *
            FROM liquidacion_totalizado
        ) lt ON lt.origen_liquidacion_id_liquidacion = l.id_liquidacion
                 LEFT JOIN (
            SELECT
                le.origen_totalizado_id,
                STRING_AGG(
                e.id_empleado || ' - ' || e.nombre || ' ' || e.primer_apellido,
                ', ' ORDER BY e.nombre
                          ) FILTER (WHERE le.fue_chofer = true)  AS chofer,
                STRING_AGG(
                e.id_empleado || ' - ' || e.nombre || ' ' || e.primer_apellido,
                ', ' ORDER BY e.nombre
                          ) FILTER (WHERE le.fue_chofer = false) AS ayudantes
            FROM liquidacion_totalizado_empleado le
                     JOIN empleados e ON e.id = le.empleado_id
            GROUP BY le.origen_totalizado_id
        ) emp ON emp.origen_totalizado_id = lt.id
                 LEFT JOIN liquidacion_distribucion_descuento d
                           ON lp.id_partida = d.liquidacion_partida_entity_id_partida

        WHERE COALESCE(lp.cancelada, false) = false
          AND COALESCE(l.estados_liquidacion, '') <> 'CANCELADA'
          AND (p_clientes_ids IS NULL OR c.id = ANY (p_clientes_ids))
          AND (p_productos_ids IS NULL OR lp.producto_comercial_id = ANY (p_productos_ids))
          AND (p_ruta_ids IS NULL OR r.id = ANY (p_ruta_ids))
          AND (p_inicio_fecha IS NULL OR l.fecha >= p_inicio_fecha)
          AND (p_fin_fecha IS NULL OR l.fecha <= p_fin_fecha)
          AND (p_conceptos_venta IS NULL OR lcv.id = ANY (p_conceptos_venta))
          AND (p_facturados IS NULL OR lp.factura = p_facturados)
          AND (p_tipos_pago IS NULL OR lp.forma_pago = ANY (p_tipos_pago))
          AND (p_estado_liquidacion IS NULL OR l.estados_liquidacion = p_estado_liquidacion)
        ORDER BY l.fecha DESC;
END;
$$ LANGUAGE plpgsql;

-- TEST STORE
SELECT * FROM get_liquidaciones_partidas(
        NULL::uuid[],
        NULL::uuid[],
        NULL::uuid[],
        '2026-05-12T00:00:00.000Z'::timestamp,
        '2026-05-12T23:59:59.999Z'::timestamp,
        NULL::uuid[],
        null::boolean,
        array ['CONTADO']::varchar[],
        'FINALIZADA'::varchar,
        'xyz'::varchar
              );