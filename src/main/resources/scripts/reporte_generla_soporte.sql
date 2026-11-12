-- FUNCTION LIQUIDACIONES


drop function get_liquidaciones_finalizadas;

CREATE OR REPLACE FUNCTION public.get_liquidaciones_finalizadas(
    p_fecha_inicial timestamp without time zone,
    p_fecha_final timestamp without time zone
)
    RETURNS SETOF liquidacion
    LANGUAGE plpgsql
AS
$$
BEGIN
    RETURN QUERY
        SELECT *
        FROM liquidacion
        WHERE estados_liquidacion = 'FINALIZADA'
          AND fecha BETWEEN p_fecha_inicial AND p_fecha_final;
END;
$$;

-- Ejemplo:
SELECT * FROM get_liquidaciones_finalizadas('2026-01-15', '2026-05-15');


DROP FUNCTION IF EXISTS public.get_totalizados_choferes_por_fecha(timestamp without time zone, timestamp without time zone);

CREATE OR REPLACE FUNCTION public.get_totalizados_choferes_por_fecha(
    p_fecha_inicial timestamp without time zone,
    p_fecha_final timestamp without time zone
)
    RETURNS TABLE
            (
                id                                uuid,
                empleado_id                       uuid,
                cantidad                          numeric,
                fue_chofer                        boolean,
                chofer_origen_id                  uuid,
                origen_totalizado_id              uuid,
                origen_liquidacion_id_liquidacion uuid,
                producto_comercial_id             uuid
            )
    LANGUAGE plpgsql
AS
$$
BEGIN
    RETURN QUERY
        SELECT lte.id,
               lte.empleado_id,
               lte.cantidad,
               lte.fue_chofer,
               lte.chofer_origen_id,
               lte.origen_totalizado_id,
               lt.origen_liquidacion_id_liquidacion,
               lt.producto_comercial_id
        FROM liquidacion_totalizado_empleado lte
                 INNER JOIN liquidacion_totalizado lt ON lte.origen_totalizado_id = lt.id
                 INNER JOIN get_liquidaciones_finalizadas(p_fecha_inicial, p_fecha_final) lpadre
                            ON lpadre.id_liquidacion = lt.origen_liquidacion_id_liquidacion
        WHERE lte.fue_chofer = TRUE;
END;
$$;

SELECT * FROM get_totalizados_choferes_por_fecha('2026-01-15', '2026-05-15');