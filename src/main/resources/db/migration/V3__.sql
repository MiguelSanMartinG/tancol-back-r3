INSERT INTO public.categoria_producto (id, enabled, deleted, created_at, updated_at, nombre)
VALUES ('bf323aa2-d19a-44f2-90c6-f9583a58faa9', true, false, '2025-03-06 12:52:31.148866', '2025-03-06 12:52:31.148866',
        'General');

INSERT INTO public.productos (id, enabled, deleted, created_at, updated_at, nombre, descripcion, sku, alias,
                              categoria_producto_id)
VALUES ('be1a7022-47b4-4ad5-a1cb-4a3772d52eac', true, false, '2025-03-06 12:57:30.402465', '2025-03-06 12:57:30.402465',
        'Botellon', 'Botellon Prueba', 'AU-10', 'Botellon', 'bf323aa2-d19a-44f2-90c6-f9583a58faa9');

INSERT INTO public.proveedores (id, enabled, deleted, created_at, updated_at, nombre, razon_social)
VALUES ('68c0ca9c-8d23-4499-9756-4846f03f69c6', true, false, '2025-03-06 12:58:57.304478', '2025-03-06 12:58:57.304478',
        'Jorgito S.A de C.V.', 'La galletada');

INSERT INTO public.tipo_impuesto (id, enabled, deleted, created_at, updated_at, nombre, porcentaje)
VALUES ('1e023295-22c4-4376-b682-6ed15ec38543', true, false, '2025-03-06 13:00:33.171164', '2025-03-06 13:00:33.171164',
        'IVA', 50);