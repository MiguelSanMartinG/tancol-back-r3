-- Reporte Bitácoras de Ventas
-- Filtros: fecha inicio/fin, folio, ruta, proceso, chofer, ayudante, producto, vehículo, fecha firma, rotos, fuera de especificación, roto por máquina

DROP FUNCTION IF EXISTS public.get_bitacoras_ventas(
    timestamp, timestamp, varchar, uuid[], varchar[], uuid[], uuid[], uuid[], uuid[], timestamp, boolean, boolean
);
DROP FUNCTION IF EXISTS public.get_bitacoras_ventas(
    timestamp, timestamp, varchar, uuid[], varchar[], uuid[], uuid[], uuid[], uuid[], timestamp, boolean, boolean, boolean
);

CREATE OR REPLACE FUNCTION public.get_bitacoras_ventas(
    p_inicio_fecha timestamp DEFAULT NULL::timestamp without time zone,
    p_fin_fecha timestamp DEFAULT NULL::timestamp without time zone,
    p_folio varchar DEFAULT NULL::varchar,
    p_ruta_ids uuid[] DEFAULT NULL::uuid[],
    p_procesos varchar[] DEFAULT NULL::varchar[],
    p_chofer_ids uuid[] DEFAULT NULL::uuid[],
    p_ayudante_ids uuid[] DEFAULT NULL::uuid[],
    p_producto_ids uuid[] DEFAULT NULL::uuid[],
    p_vehiculo_ids uuid[] DEFAULT NULL::uuid[],
    p_fecha_firma timestamp DEFAULT NULL::timestamp without time zone,
    p_rotos boolean DEFAULT NULL,
    p_fuera_especificacion boolean DEFAULT NULL,
    p_roto_por_maquina boolean DEFAULT NULL
)
    RETURNS TABLE
            (
                id_bitacora                    uuid,
                id_trayecto                    uuid,
                id_venta                       uuid,
                folio                          varchar,
                fecha                          date,
                estatus_bitacora               varchar,
                nombre_ruta                    varchar,
                tipo_proceso                   varchar,
                chofer                         varchar,
                ayudantes                      varchar,
                nombre_producto                varchar,
                vehiculo                       varchar,
                fecha_firma                    timestamp,
                cantidad_salida                numeric,
                cantidad_llegada               numeric,
                cantidad_vendida               numeric,
                huecos                         numeric,
                rotos                          numeric,
                fuera_especificacion           varchar,
                cantidad_fuera_especificacion  numeric,
                estado_venta_ruta              varchar,
                mandado_a_liquidacion          boolean,
                comentario                     varchar
            )
AS
$$
BEGIN
    RETURN QUERY
        SELECT b.id_bitacora,
               t.id_trayecto,
               vp.id_venta,
               b.folio,
               b.fecha,
               b.estatus_bitacora_venta                         AS estatus_bitacora,
               r.nombre                                         AS nombre_ruta,
               t.tipo_proceso::varchar,
               NULLIF(concat_ws(' ', chofer_emp.nombre, chofer_emp.primer_apellido,
                                chofer_emp.segundo_apellido), '')::varchar AS chofer,
               ayu.ayudantes::varchar                           AS ayudantes,
               COALESCE(pc.alias, pc.nombre)::varchar           AS nombre_producto,
               NULLIF(concat_ws(' - ', veh.nombre, veh.placas), '')::varchar AS vehiculo,
               t.singed_at                                      AS fecha_firma,
               vp.cantidad_salida,
               vp.cantidad_llegada,
               vp.cantidad                                      AS cantidad_vendida,
               vp.huecos,
               COALESCE(mer.rotos_total, 0)                     AS rotos,
               pfe.detalle                                      AS fuera_especificacion,
               pfe.cantidad_total                               AS cantidad_fuera_especificacion,
               t.estado_venta_ruta,
               COALESCE(
                       (to_jsonb(t) ->> 'mandado_a_liquidacion')::boolean,
                       (to_jsonb(t) ->> 'mandadoaliquidacion')::boolean
               )                                                AS mandado_a_liquidacion,
               t.comentario
        FROM bitacora_rutas_ventas b
                 INNER JOIN bitacora_rutas_ventas_trayectos t
                            ON t.bitacora_venta_ruta_entity_id_bitacora = b.id_bitacora
                 LEFT JOIN bitacora_rutas_ventas_trayectos_producto vp
                           ON vp.trayecto_origen_id_trayecto = t.id_trayecto
                 LEFT JOIN rutas r ON r.id = t.ruta_id
                 LEFT JOIN empleados chofer_emp ON chofer_emp.id = t.chofer_id
                 LEFT JOIN vehiculos veh ON veh.id = t.vehiculo_id
                 LEFT JOIN productos_comerciales pc ON pc.id = vp.producto_id
                 LEFT JOIN (
            SELECT cer.trayecto_origen_id_venta,
                   STRING_AGG(
                           NULLIF(concat_ws(' ', e.nombre, e.primer_apellido, e.segundo_apellido), ''),
                           ', ' ORDER BY e.nombre
                   ) AS ayudantes
            FROM comisiones_empleado_ruta cer
                     JOIN empleados e ON e.id = cer.empleado_id
            GROUP BY cer.trayecto_origen_id_venta
        ) ayu ON ayu.trayecto_origen_id_venta = vp.id_venta
                 LEFT JOIN (
            SELECT p.venta_trayecto_entity_id_venta,
                   STRING_AGG(
                           CONCAT(COALESCE(pr.motivo, 'Sin concepto'), ' (', p.cantidad, ')'),
                           ', ' ORDER BY pr.motivo
                   )::varchar AS detalle,
                   SUM(p.cantidad) AS cantidad_total
            FROM productos_fuera_especificacion p
                     LEFT JOIN producto_rezagado pr ON pr.id = p.concepto_id
            GROUP BY p.venta_trayecto_entity_id_venta
        ) pfe ON pfe.venta_trayecto_entity_id_venta = vp.id_venta
                 LEFT JOIN (
            SELECT m.venta_trayecto_id,
                   SUM(m.cantidad) AS rotos_total
            FROM bitacora_venta_trayecto_mermas m
            GROUP BY m.venta_trayecto_id
        ) mer ON mer.venta_trayecto_id = vp.id_venta
        WHERE (p_inicio_fecha IS NULL OR b.fecha >= p_inicio_fecha::date)
          AND (p_fin_fecha IS NULL OR b.fecha <= p_fin_fecha::date)
          AND (p_folio IS NULL OR btrim(p_folio) = '' OR b.folio ILIKE '%' || btrim(p_folio) || '%')
          AND (p_ruta_ids IS NULL OR cardinality(p_ruta_ids) = 0 OR t.ruta_id = ANY (p_ruta_ids))
          AND (p_procesos IS NULL OR cardinality(p_procesos) = 0 OR t.tipo_proceso::text = ANY (p_procesos))
          AND (p_chofer_ids IS NULL OR cardinality(p_chofer_ids) = 0 OR t.chofer_id = ANY (p_chofer_ids))
          AND (p_ayudante_ids IS NULL OR cardinality(p_ayudante_ids) = 0 OR EXISTS (
                SELECT 1
                FROM comisiones_empleado_ruta cer
                WHERE cer.trayecto_origen_id_venta = vp.id_venta
                  AND cer.empleado_id = ANY (p_ayudante_ids)
            ) OR EXISTS (
                SELECT 1
                FROM rutas_empleados re
                WHERE re.rutas_id = t.ruta_id
                  AND re.empleados_id = ANY (p_ayudante_ids)
            ))
          AND (p_producto_ids IS NULL OR cardinality(p_producto_ids) = 0 OR vp.producto_id = ANY (p_producto_ids))
          AND (p_vehiculo_ids IS NULL OR cardinality(p_vehiculo_ids) = 0 OR t.vehiculo_id = ANY (p_vehiculo_ids))
          AND (p_fecha_firma IS NULL OR t.singed_at::date = p_fecha_firma::date)
          AND (p_rotos IS NULL
            OR (p_rotos AND COALESCE(mer.rotos_total, 0) > 0)
            OR (NOT p_rotos AND COALESCE(mer.rotos_total, 0) = 0))
          AND (p_fuera_especificacion IS NULL
            OR (p_fuera_especificacion AND EXISTS (
                SELECT 1
                FROM productos_fuera_especificacion pfe_f
                WHERE pfe_f.venta_trayecto_entity_id_venta = vp.id_venta
            ))
            OR (NOT p_fuera_especificacion AND NOT EXISTS (
                SELECT 1
                FROM productos_fuera_especificacion pfe_f
                WHERE pfe_f.venta_trayecto_entity_id_venta = vp.id_venta
            )))
          AND (p_roto_por_maquina IS NULL
            OR (p_roto_por_maquina AND EXISTS (
                SELECT 1
                FROM bitacora_venta_trayecto_mermas m
                WHERE m.venta_trayecto_id = vp.id_venta
                  AND m.roto_por_maquina
            ))
            OR (NOT p_roto_por_maquina AND NOT EXISTS (
                SELECT 1
                FROM bitacora_venta_trayecto_mermas m
                WHERE m.venta_trayecto_id = vp.id_venta
                  AND m.roto_por_maquina
            )))
        ORDER BY b.fecha DESC, b.folio, r.nombre, pc.alias;
END;
$$ LANGUAGE plpgsql;
