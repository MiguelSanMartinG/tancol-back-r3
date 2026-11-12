# Integración: Insumos, stock mínimo y notificación

Campos nuevos del catálogo de insumos. Si el checkbox está marcado, el backend revisa el stock total cada vez que ese producto sale de un almacén y, si quedó bajo el mínimo, crea la notificación.

Requiere JWT.

```
Authorization: Bearer <token>
Content-Type: application/json
```

Base local: `/api/v1`. Producción: `https://back.purificadoratancol.com/api/v1`.

## Qué hace el front

1. En el formulario del insumo: marca, modelo, stock mínimo, stock máximo y el checkbox `notificarStockMinimo`.
2. Guardar el insumo. El checkbox no crea la alerta; solo la autoriza.
3. No hay que llamar nada extra al registrar una salida, una transferencia, un ajuste a la baja o una recepción que reduzca unidades. El backend revisa ese insumo al terminar la operación.
4. La campana sigue siendo `GET /notificaciones`. El tipo nuevo es `STOCK_BAJO_MINIMO` y `productoId` abre el insumo.
5. El botón “Revisar stock mínimo” es opcional. Sirve para revisar todos los insumos marcados de una sola vez.

## Pantalla de insumos

| Control | Campo | Notas |
| --- | --- | --- |
| Texto | `marca` | Opcional |
| Texto | `modelo` | Opcional |
| Número | `stockMinimo` | Opcional. `>= 0`. Obligatorio si el checkbox está marcado |
| Número | `stockMaximo` | Opcional. `>= 0` y `>= stockMinimo`. No dispara la alerta |
| Checkbox | `notificarStockMinimo` | Apagado por defecto |

El `PUT` reemplaza el insumo completo. Hay que reenviar `notificarStockMinimo` con el valor actual del checkbox. Si el campo no viaja, el backend lo guarda en `false` y apaga la alerta.

## Alta y edición

```
POST /api/v1/producto
PUT  /api/v1/producto/{id}
```

Request:

```json
{
  "nombre": "Filtro de sedimentos",
  "descripcion": "Cartucho 10 pulgadas",
  "sku": "FIL-10",
  "alias": "FILTRO",
  "categoriaProductoId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
  "idImagen": null,
  "unidadMedidaDefault": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "marca": "Pentair",
  "modelo": "P5",
  "stockMinimo": 10,
  "stockMaximo": 40,
  "notificarStockMinimo": true
}
```

| Campo | Tipo | Obligatorio |
| --- | --- | --- |
| `nombre` | string | Sí |
| `categoriaProductoId` | UUID | Sí |
| `descripcion`, `sku`, `alias`, `idImagen` | string | No |
| `unidadMedidaDefault` | UUID | No |
| `marca`, `modelo` | string | No |
| `stockMinimo`, `stockMaximo` | number | No |
| `notificarStockMinimo` | boolean | No. Ausente o `null` = `false` |

`POST` responde `201`. `PUT` responde `200`. El cuerpo es el insumo:

```json
{
  "idProducto": "8d798c8d-7982-4a8d-9d86-475d83c0c5a2",
  "nombre": "Filtro de sedimentos",
  "alias": "FILTRO",
  "descripcion": "Cartucho 10 pulgadas",
  "sku": "FIL-10",
  "nombreCategoria": "Refacciones",
  "createdAt": "2026-09-28T15:40:00",
  "updatedAt": "2026-09-28T15:40:00",
  "idImagen": null,
  "enabled": true,
  "unidadMedidaDefault": {
    "idUnidadDeMedida": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "nombre": "Pieza",
    "alias": "PZA",
    "updatedAt": "2026-09-01T10:00:00",
    "createdAt": "2026-09-01T10:00:00"
  },
  "marca": "Pentair",
  "modelo": "P5",
  "stockMinimo": 10,
  "stockMaximo": 40,
  "notificarStockMinimo": true
}
```

Consulta:

```
GET /api/v1/producto
GET /api/v1/producto/{id}
```

`GET /producto` acepta solo estos query params: `id`, `nombre`, `enabled`, `sku`, `alias`. Cualquier otro filtro responde error.

Los mismos campos (`marca`, `modelo`, `stockMinimo`, `stockMaximo`, `notificarStockMinimo`) también vienen dentro del producto anidado en inventario, partidas de orden interna y partidas de transferencia.

## Checkbox

| Acción del usuario | Qué hacer | Efecto |
| --- | --- | --- |
| Marcar el checkbox | `PUT` con `notificarStockMinimo: true` y `stockMinimo` | A partir de la próxima salida se revisa ese insumo |
| Desmarcar el checkbox | `PUT` con `notificarStockMinimo: false` | Deja de revisarse. Las alertas de ese insumo que aún no se leyeron salen de la bandeja |
| Eliminar el insumo | `DELETE /api/v1/producto/{id}` | Igual que desmarcar |

Las notificaciones ya leídas se conservan como historial. Marcar una como leída no apaga el checkbox.

## Cuándo se crea la alerta sola

El front no llama un endpoint nuevo. Si el checkbox está marcado, el backend revisa ese insumo al terminar:

- Salida de material (orden interna). Descuenta el almacén.
- Transferencia entre almacenes. Descuenta el almacén de origen.
- Ajuste de inventario que deja menos cantidad que antes.
- Recepción de compra que reduce unidades ya recibidas.

La comparación es el stock total de todos los almacenes contra `stockMinimo`. Si no hay inventario, el stock es `0`. Si la suma es menor que el mínimo y no hay ya una alerta `STOCK_BAJO_MINIMO` sin leer de ese insumo, se crea una notificación por cada empleado con lectura en el módulo `Insumos`.

Mientras esa alerta siga sin leerse, otra salida del mismo insumo no duplica el aviso. Si la marcan como leída y el producto vuelve a salir estando bajo el mínimo, se crea otra.

Una transferencia no cambia el stock total. Solo genera alerta si el total ya estaba bajo el mínimo y no había un aviso pendiente.

Entradas de mercancía, altas de inventario y aumentos de cantidad no revisan el mínimo.

## Revisión manual

Botón opcional, por ejemplo “Revisar stock mínimo”. No lleva cuerpo.

```
POST /api/v1/producto/revisar-stock-minimo
```

Qué hace:

1. Toma insumos activos, no eliminados, con el checkbox marcado y con `stockMinimo`.
2. Suma `cantidad` del inventario en todos los almacenes no eliminados.
3. Si la suma es menor que `stockMinimo`, crea una notificación `STOCK_BAJO_MINIMO` por cada empleado con lectura en el módulo `Insumos`.

A diferencia de la salida automática, cada clic genera notificaciones nuevas aunque ya existan iguales.

Response `200`:

```json
{
  "ejecutadoEn": "2026-09-28T15:45:00",
  "totalEvaluadas": 12,
  "totalBajoMinimo": 1,
  "destinatariosEncontrados": 3,
  "notificacionesGeneradas": 3,
  "bajoMinimo": [
    {
      "idProducto": "8d798c8d-7982-4a8d-9d86-475d83c0c5a2",
      "nombre": "Filtro de sedimentos",
      "sku": "FIL-10",
      "stockMinimo": 10,
      "stockActual": 4
    }
  ]
}
```

| Campo | Uso en pantalla |
| --- | --- |
| `totalEvaluadas` | Insumos con checkbox activo y stock mínimo |
| `totalBajoMinimo` | Cuántos quedaron debajo |
| `destinatariosEncontrados` | Empleados que pueden ver la alerta. `0` significa que nadie tiene lectura en el módulo Insumos |
| `notificacionesGeneradas` | `totalBajoMinimo` × destinatarios |
| `bajoMinimo` | Lista para mostrar nombre, SKU, stock actual y mínimo |

Si `destinatariosEncontrados` es `0` y hay productos bajos, no se crea ninguna notificación. Conviene mostrar ese caso. La salida automática se comporta igual: si no hay destinatarios, la operación de almacén sí se guarda y no aparece alerta.

## Bandeja de notificaciones

```
GET /api/v1/notificaciones
PUT /api/v1/notificaciones/{id}/leer
```

Ejemplo:

```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "titulo": "Stock bajo el mínimo",
  "mensaje": "El insumo Filtro de sedimentos (SKU: FIL-10) está bajo el stock mínimo. Stock actual: 4, mínimo: 10.",
  "tipo": "STOCK_BAJO_MINIMO",
  "leida": false,
  "asignacionId": null,
  "clienteId": null,
  "metaVentaId": null,
  "productoId": "8d798c8d-7982-4a8d-9d86-475d83c0c5a2",
  "periodoInicio": null,
  "periodoFin": null,
  "createdAt": "2026-09-28T15:45:00",
  "updatedAt": "2026-09-28T15:45:00"
}
```

`tipo` posibles: `EQUIPO_MONTO_NO_CUBIERTO`, `META_VENTA_NO_CUBIERTA`, `STOCK_BAJO_MINIMO`.

Para `STOCK_BAJO_MINIMO`, usar `productoId` para abrir el insumo.

## Validaciones antes de guardar

| Regla | Respuesta del API |
| --- | --- |
| `stockMinimo` y `stockMaximo` no negativos | `400` con `{ "stockMinimo": "El stock mínimo no puede ser negativo" }` |
| Si ambos vienen, `stockMaximo >= stockMinimo` | Mensaje: `El stock máximo debe ser mayor o igual al stock mínimo` |
| Checkbox activo solo si hay `stockMinimo` | Mensaje: `Para activar la notificación debe indicar el stock mínimo` |
| Nombre único | `409` si el nombre ya existe |
