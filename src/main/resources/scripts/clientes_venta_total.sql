--  SCRIPT 2 --- VENTAS/CLIENTE
SELECT p.alias,p.nombre,ct.nombre_comercial, ct.razon_social_fiscal,
    SUM(lp.cantidad) as cantidad_vendida
--        concat(e.nombre, ' ', e.primer_apellido, ' ', e.segundo_apellido) as nombre,
--        p.alias           as producto,
--        SUM(lte.cantidad) as cantidad_vendida
FROM liquidacion_partidas lp
         left join clientes ct ON lp.cliente_id = ct.id
         left join liquidacion l ON lp.liquidacion_origen_id_liquidacion = l.id_liquidacion
         left join productos_comerciales p ON lp.producto_comercial_id = p.id
WHERE p.id IS NOT NULL AND  l.estados_liquidacion = 'FINALIZADA'
GROUP BY  p.alias,p.nombre,ct.nombre_comercial, ct.razon_social_fiscal;

drop function get_ventas_clientes;

CREATE OR REPLACE FUNCTION public.get_ventas_clientes(
    clientes_id uuid[] DEFAULT NULL::uuid[],
    productos_ids uuid[] default null::uuid[],
    p_inicio_fecha timestamp DEFAULT NULL::timestamp without time zone,
    p_fin_fecha timestamp DEFAULT NULL::timestamp without time zone
)
    RETURNS TABLE
            (
                alias      varchar,
                nombre           varchar,
                nombre_comercial         varchar,
                razon_social varchar,
                cantidad_vendida numeric
            )
as
$$
BEGIN
    RETURN QUERY
        SELECT p.alias as alias ,p.nombre as nombre_producto,
               ct.nombre_comercial as nombre_comercial,
               ct.razon_social_fiscal as razon_social,
               SUM(lp.cantidad) as cantidad_vendida
        FROM liquidacion_partidas lp
                 left join clientes ct ON lp.cliente_id = ct.id
                 left join liquidacion l ON lp.liquidacion_origen_id_liquidacion = l.id_liquidacion
                 left join productos_comerciales p ON lp.producto_comercial_id = p.id
        WHERE
          -- Filtro por clientes (si es null, trae todos).
            (clientes_id IS NULL OR ct.id = ANY (clientes_id))
          AND (productos_ids IS NULL OR p.id = ANY (productos_ids))
          -- Filtro por rango de fechas.
          AND (p_inicio_fecha IS NULL OR l.fecha >= p_inicio_fecha)
          AND (p_fin_fecha IS NULL OR l.fecha <= p_fin_fecha)
          AND (l.estados_liquidacion = 'FINALIZADA')
        GROUP BY p.alias, p.nombre, ct.nombre_comercial, p.nombre, p.alias, ct.razon_social_fiscal
        ORDER BY ct.razon_social_fiscal DESC;
END;
$$ language plpgsql;

select * from get_ventas_clientes(null,
                                   null,
                                   '2025-07-08T21:54:13.246Z',
                                   '2025-12-08T21:54:13.246Z');