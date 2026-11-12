# Integración: notificaciones por rol

La campana deja de mostrarse solo para Admin. El módulo `Notificaciones` se asigna a un rol como cualquier otro módulo. Además, cada rol elige qué tipos de aviso ve.

Requiere JWT.

```
Authorization: Bearer <token>
Content-Type: application/json
```

Base local: `/api/v1`. Producción: `https://back.purificadoratancol.com/api/v1`.

## Qué hace el front

1. Mostrar el módulo en el menú solo si el rol del usuario tiene un perfil de `Notificaciones` con `canRead` en `true`.
2. La campana sigue siendo `GET /notificaciones`. Si el rol no puede leer el módulo, o no tiene tipos asignados, la lista llega vacía. No es un error.
3. En la pantalla del rol, además del permiso de ver el módulo, guardar qué códigos de aviso recibe ese rol.
4. El nombre y la descripción de cada tipo se pueden editar. Esa descripción es la misma para todos los avisos de ese tipo. El `titulo` y el `mensaje` de cada aviso siguen siendo el texto concreto del evento.

## Menú

El módulo ya existe en catálogo.

| Campo | Valor |
| --- | --- |
| `name` | `Notificaciones` |
| `ruta` | `notificaciones` |
| Padre | ninguno |

Se asigna con el mismo `POST /Profile` del resto de módulos. `canRead` es el que muestra el menú y permite listar la campana.

```
GET /Profile?idRole={roleId}
```

`roleId` es el id numérico del rol. En la respuesta, el módulo viene en `module.name` y `module.ruta`. El permiso de ver es `canRead`.

Un rol puede tener el módulo y aun así ver la campana vacía, si no tiene tipos asignados. Quitar tipos no quita el módulo: son dos cosas distintas.

## Campana

```
GET /api/v1/notificaciones
PUT /api/v1/notificaciones/{id}/leer
```

`GET` devuelve solo los avisos del usuario autenticado cuyo `tipo` está asignado a su rol, y solo si ese rol tiene `canRead` en Notificaciones. Orden: de la más reciente a la más antigua. No incluye filas eliminadas.

```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "titulo": "Stock bajo el mínimo",
  "mensaje": "El insumo Filtro de sedimentos (SKU: FIL-10) está bajo el stock mínimo. Stock actual: 4, mínimo: 10.",
  "descripcion": "Insumo bajo el mínimo",
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

| Campo | Uso |
| --- | --- |
| `titulo` | Título de este aviso |
| `mensaje` | Texto de este aviso |
| `descripcion` | Texto del tipo, editable en el catálogo. Puede ser `null` |
| `tipo` | Código. Ver la tabla de tipos |
| `leida` | `false` hasta que se marque |
| `productoId` | Insumo en `STOCK_BAJO_MINIMO`. Producto comercial en `BOTELLON_ROTO_SOBRE_TOLERANCIA` |
| `metaVentaId` | Meta en `META_VENTA_NO_CUBIERTA` |
| `asignacionId`, `clienteId` | Equipo y cliente en `EQUIPO_MONTO_NO_CUBIERTO` |
| `periodoInicio`, `periodoFin` | Rango del aviso, cuando aplica |

`PUT /notificaciones/{id}/leer` marca como leída una fila propia cuyo tipo sigue asignado al rol. Responde `200` con el mismo objeto y `leida: true`. Si la fila no es del usuario, ya no existe o el rol ya no tiene ese tipo, responde `409` con `message`: `No se encontró la notificación`.

## Catálogo de tipos

```
GET /api/v1/notificaciones/tipos
PUT /api/v1/notificaciones/tipos/{codigo}
```

`GET` responde `200` con la lista ordenada por nombre.

```json
[
  {
    "id": "c9e503f4-6d7a-412c-9e43-3f80ac2d5b33",
    "codigo": "STOCK_BAJO_MINIMO",
    "nombre": "Stock bajo el mínimo",
    "descripcion": "Insumo bajo el mínimo"
  }
]
```

`PUT` cambia nombre y descripción. El código no se cambia. Responde `200` con el tipo actualizado.

```json
{
  "nombre": "Stock bajo el mínimo",
  "descripcion": "Aviso cuando un insumo queda por debajo de su mínimo"
}
```

| Campo | Tipo | Obligatorio |
| --- | --- | --- |
| `nombre` | string | Sí. Máximo 255 |
| `descripcion` | string | No. Máximo 2000. Vacío o ausente la deja en `null` |

Códigos que existen hoy:

| Código | Nombre inicial | Descripción inicial |
| --- | --- | --- |
| `EQUIPO_MONTO_NO_CUBIERTO` | Equipo sin cobertura | Cobertura de equipos no cubierta |
| `META_VENTA_NO_CUBIERTA` | Meta de venta no cubierta | Meta de venta no cubierta |
| `STOCK_BAJO_MINIMO` | Stock bajo el mínimo | Insumo bajo el mínimo |
| `BOTELLON_ROTO_SOBRE_TOLERANCIA` | Botellones rotos sobre la tolerancia | Botellones rotos sobre la tolerancia |

El front debe pintar los checkboxes con `GET /notificaciones/tipos`. No hay que dejar la lista fija en código: el nombre y la descripción pueden cambiar.

## Tipos que ve cada rol

```
GET /api/v1/notificaciones/roles/{roleId}
PUT /api/v1/notificaciones/roles/{roleId}
```

`roleId` es el id numérico del rol, el mismo de `GET /rol`.

`GET` responde `200` con los tipos asignados, en el mismo formato del catálogo. Si el rol no tiene ninguno, la lista llega vacía.

`PUT` reemplaza la lista completa. Hay que enviar todos los códigos que deben quedar, no solo el que se marcó o se desmarcó. Una lista vacía quita todos los tipos. Responde `200` con los tipos que quedaron.

```json
{
  "tipos": ["STOCK_BAJO_MINIMO", "META_VENTA_NO_CUBIERTA"]
}
```

| Campo | Tipo | Obligatorio |
| --- | --- | --- |
| `tipos` | array de string | Sí. Puede ir vacío. Cada valor es un `codigo` del catálogo |

Los códigos se aceptan en cualquier mayúscula o minúscula. Un código repetido se guarda una sola vez.

## Quién recibe avisos nuevos

El backend crea el aviso para los empleados activos cuyo rol tiene ese código y, además, `canRead` en Notificaciones. Si el rol tiene el tipo pero no el módulo, no recibe el aviso. Si tiene el módulo pero no el tipo, tampoco.

Al aplicar la migración, un rol que ya tenía lectura en el módulo de origen queda con el módulo Notificaciones y con ese tipo:

| Módulo que ya podía leer | Tipo que se le asigna |
| --- | --- |
| Asignacion de equipos | `EQUIPO_MONTO_NO_CUBIERTO` |
| Metas de ventas | `META_VENTA_NO_CUBIERTA` |
| Insumos | `STOCK_BAJO_MINIMO` |
| Bitácora de Venta | `BOTELLON_ROTO_SOBRE_TOLERANCIA` |

Un rol sin esa lectura no ve el módulo hasta que se le cree el perfil y se le asignen tipos.

## Errores

| Caso | Respuesta |
| --- | --- |
| Falta `nombre`, pasa de 255, o `descripcion` pasa de 2000 | `400`. Mapa de campo a mensaje |
| Falta `tipos` o el cuerpo no es JSON | `400` |
| Rol inexistente | `409`. `message`: `No se encontró el rol` |
| Código que no está en el catálogo | `409`. `message`: `No se encontró el tipo de notificación` o el mismo texto más `: CODIGO` |
| Código que no es uno de los cuatro tipos | `500`. Enviar solo códigos devueltos por `GET /notificaciones/tipos` |
| Marcar leída una notificación ajena, borrada o de un tipo que el rol ya no tiene | `409`. `message`: `No se encontró la notificación` |

El `409` trae `status`, `message`, `path` y `timestamp`.
