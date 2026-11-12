CREATE OR REPLACE FUNCTION public.get_liquidaciones_partidas(
    p_clientes_ids uuid[] DEFAULT NULL::uuid[],
    p_ruta_ids uuid[] default null::uuid[],
    p_productos_ids uuid[] default null::uuid[],
    p_inicio_fecha timestamp DEFAULT NULL::timestamp without time zone,
    p_fin_fecha timestamp DEFAULT NULL::timestamp without time zone
)
    RETURNS TABLE
            (
                folio           varchar,
                bloque_nota     varchar,
                nombre_producto varchar,
                importe         numeric,
                precio          numeric,
                cantidad        numeric,
                nombre_cliente  varchar,
                fecha           timestamp
            )
as
$$
BEGIN
    RETURN QUERY
        SELECT l.folio                              as folio,
               l.bloque_nota                        as bloque_nota,
               r.nombre                             as nombre_ruta,
               pc.alias                             as nombre_producto,
               lp.importe                           as importe,
               lp.precio                            as precio,
               lp.cantidad                          as cantidad,
               c.nombre_comercial                   as nombre_cliente,
               l.fecha::timestamp without time zone as fecha

        FROM liquidacion_partidas lp
                 left join liquidacion l on liquidacion_origen_id_liquidacion = l.id_liquidacion
                 left join productos_comerciales pc on lp.producto_comercial_id = pc.id
                 left join clientes c on lp.cliente_id = c.id
                 left join rutas r on r.id = l.ruta_id
        WHERE
           -- Filtro por clientes (si es null, trae todos).
            (p_clientes_ids IS NULL OR c.id = ANY (p_clientes_ids))
                AND (p_productos_ids IS NULL OR lp.producto_comercial_id = ANY (p_productos_ids)) AND
            (p_ruta_ids) IS NULL
           OR r.id = ANY (p_ruta_ids)
            -- Filtro por rango de fechas.
            AND (p_inicio_fecha IS NULL OR l.fecha >= p_inicio_fecha)
            AND (p_fin_fecha IS NULL OR l.fecha <= p_fin_fecha)
        ORDER BY l.fecha DESC;
END;
$$ language plpgsql;