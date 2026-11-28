-- Inventario diario: ajustes del 27/28-11-2026 (Buzón 9 del front).
-- - Las partidas descuentan del cupo y del almacén al firmarse (antes, al guardarse).
-- - Quién agregó cada tarjeta (solo él la elimina).
-- - Firma de entrega del responsable del almacén en las solicitudes.
-- Idempotente, como V25.

-- Partidas: «descontado» indica si la partida ya movió el cupo y la existencia del almacén.
-- Todas las partidas que ya existen se descontaron al guardarse (regla anterior), firmadas o no:
-- se marcan para no descontarlas dos veces al firmarlas.
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name = 'inv_diario_partidas' AND column_name = 'descontado') THEN
        ALTER TABLE inv_diario_partidas ADD COLUMN descontado BOOLEAN NOT NULL DEFAULT false;
        UPDATE inv_diario_partidas SET descontado = true;
    END IF;
END $$;

ALTER TABLE inv_diario_cupos ADD COLUMN IF NOT EXISTS creado_por_id UUID;
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_idc_creado_por') THEN
        ALTER TABLE inv_diario_cupos ADD CONSTRAINT fk_idc_creado_por FOREIGN KEY (creado_por_id) REFERENCES empleados (id);
    END IF;
END $$;

ALTER TABLE inv_diario_solicitudes ADD COLUMN IF NOT EXISTS firma_almacen VARCHAR(300);
ALTER TABLE inv_diario_solicitudes ADD COLUMN IF NOT EXISTS fecha_firma_almacen TIMESTAMP WITHOUT TIME ZONE;
ALTER TABLE inv_diario_solicitudes ADD COLUMN IF NOT EXISTS firmado_almacen_por UUID;
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_ids_firmado_almacen') THEN
        ALTER TABLE inv_diario_solicitudes ADD CONSTRAINT fk_ids_firmado_almacen FOREIGN KEY (firmado_almacen_por) REFERENCES empleados (id);
    END IF;
END $$;

-- Sin confirmación del día, «Dotación incompleta» avisa ahora de entregas recibidas de menos o declinadas.
DO $$
BEGIN
    IF to_regclass('public.notificacion_tipos') IS NOT NULL THEN
        UPDATE notificacion_tipos
        SET descripcion = 'Avisa al responsable del almacén cuando un área recibe menos de lo autorizado o declina la entrega.',
            updated_at = now()
        WHERE codigo = 'DOTACION_INCOMPLETA';
    END IF;
END $$;
