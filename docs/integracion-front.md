# Integración: cambios para el front

Contratos nuevos o ampliados. Las tablas de comisiones siguen en [integracion-tablas-comisiones.md](integracion-tablas-comisiones.md). El stock mínimo de insumos sigue en [integracion-insumos-stock-minimo.md](integracion-insumos-stock-minimo.md).

Requiere JWT.

```
Authorization: Bearer <token>
Content-Type: application/json
```

Base local: `/api/v1`. Producción: `https://back.purificadoratancol.com/api/v1`.

## 1. Vigencia del insumo

En el formulario del insumo, además de marca, modelo y stock:

| Control | Campo | Notas |
| --- | --- | --- |
| Checkbox | `tieneVigencia` | Opcional. Ausente o `null` = `false` |
| Fecha | `fechaVigencia` | `yyyy-MM-dd`. Obligatoria solo si el checkbox está marcado. Si no tiene vigencia, no enviarla |

El `PUT /producto/{id}` reemplaza el insumo completo. Si `tieneVigencia` no viaja, queda en `false` y no debe ir `fechaVigencia`.

```json
{
  "nombre": "Cloro",
  "categoriaProductoId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
  "tieneVigencia": true,
  "fechaVigencia": "2026-12-31"
}
```

La respuesta de `POST` y `PUT /producto`, y el producto anidado en inventario, partidas y precios, incluye `tieneVigencia` y `fechaVigencia`.

Si el checkbox está marcado y falta la fecha, o si la fecha viaja con el checkbox apagado: `400` con `{ "fechaVigenciaValida": "Si el insumo tiene vigencia debe indicar la fecha de vigencia; si no la tiene, no envíe la fecha" }`.

## 2. Categorías y subcategorías de insumos

El catálogo es categoría, subcategoría e insumo.

```
Artículos de limpieza
  Cepillos
    Cepillo genérico
    Cepillo para motor
    Cepillo para ropa
  Limpiadores
  Cloros
```

El insumo se sigue guardando con `categoriaProductoId` de la subcategoría (Cepillos), no de la categoría padre.

### Alta y edición de categoría

```
POST /api/v1/category
PUT  /api/v1/category/{id}
```

```json
{
  "nombre": "Cepillos",
  "enabled": true,
  "categoriaPadreId": "<id de Articulos de limpieza>"
}
```

| Campo | Tipo | Obligatorio |
| --- | --- | --- |
| `nombre` | string | Sí |
| `enabled` | boolean | En el alta el backend lo deja activo. En el `PUT` hay que reenviarlo |
| `categoriaPadreId` | UUID | No. Si no viaja, la categoría queda como raíz |

`POST` responde `201`. `PUT` responde `200`.

```json
{
  "idCategoria": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "nombre": "Cepillos",
  "enabled": true,
  "createdAt": "2026-09-29T12:00:00",
  "updatedAt": "2026-09-29T12:00:00",
  "idCategoriaPadre": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
  "nombreCategoriaPadre": "Artículos de limpieza",
  "subcategorias": [],
  "productoList": []
}
```

`subcategorias` son las hijas directas no eliminadas: `{ "idCategoria", "nombre" }`. `productoList` son los insumos colgados directo de ese nodo.

El `GET /category` sigue siendo una lista plana. Un combo que liste todo va a mezclar categorías y subcategorías. Hay que armar el árbol con `idCategoriaPadre`. Para pedir solo las hijas de un nodo:

```
GET /api/v1/category?categoriaPadreId=<uuid>
```

Filtros que ya existían: `id`, `nombre`, `enabled`.

No se puede hacer padre a sí misma ni a una de sus hijas. No se puede borrar una categoría que todavía tiene subcategorías. El mensaje es `No se puede eliminar una categoría que tiene subcategorías` o `La categoría no puede ser subcategoría de sí misma` / `de una de sus hijas`.

### Lo que ya trae el insumo

`nombreCategoria` sigue siendo el nodo directo (Cepillos). Se agregan `idCategoriaPadre` y `nombreCategoriaPadre`.

En inventario, partida de orden interna, partida de transferencia, partida de orden y precio, el producto anidado trae `categoria` con:

```json
{
  "enabled": true,
  "idCategoria": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "nombre": "Cepillos",
  "idCategoriaPadre": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
  "nombreCategoriaPadre": "Artículos de limpieza"
}
```

### Cuántos productos tiene una subcategoría

No suma inventario. Solo cuenta los insumos activos de ese nodo.

```
GET /api/v1/category/{id}/productos
```

```json
{
  "idCategoria": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "nombre": "Cepillos",
  "numeroProductos": 3,
  "productos": [
    {
      "idProducto": "8d798c8d-7982-4a8d-9d86-475d83c0c5a2",
      "nombre": "Cepillo genérico",
      "sku": "CEP-01"
    }
  ]
}
```

### Recuento por subcategoría

Una fila por hija directa. Sirve en Artículos de limpieza para ver Cepillos, Limpiadores y Cloros sin abrir cada cepillo.

```
GET /api/v1/category/{id}/recuento
```

```json
{
  "idCategoria": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
  "nombre": "Artículos de limpieza",
  "subcategorias": [
    {
      "idCategoria": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
      "nombre": "Cepillos",
      "numeroProductos": 3,
      "existencia": [
        { "unidad": "Pieza", "cantidad": 12 }
      ]
    }
  ]
}
```

`existencia` agrupa el stock de todos los almacenes por unidad de medida. Si el insumo no tiene unidad, la etiqueta es `Sin unidad`.

### Existencia detallada

```
GET /api/v1/category/{id}/existencia
```

Para Cepillos lista cada insumo con cantidad y unidad, más el total del nodo. Para Artículos de limpieza incluye también los insumos de las subcategorías.

```json
{
  "idCategoria": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "nombre": "Cepillos",
  "productos": [
    {
      "idProducto": "8d798c8d-7982-4a8d-9d86-475d83c0c5a2",
      "nombre": "Cepillo genérico",
      "cantidad": 4,
      "unidad": "Pieza"
    }
  ],
  "totales": [
    { "unidad": "Pieza", "cantidad": 12 }
  ]
}
```

### Reporte de consumo

`POST /reportes/consumo-area-producto` ya aceptaba `idCategorias`. Si se manda la categoría padre, ahora también entran los insumos de sus subcategorías. Antes solo coincidía la categoría directa del insumo.

## 3. Tabulador de botellones rotos

Una fila por empleado y producto comercial. El periodo solo admite `SEMANAL`. La tolerancia es la cantidad máxima de rotos en la semana calendario (lunes a domingo, hora de Ciudad de México).

La pantalla no dispara la alerta. Al guardar un trayecto de bitácora, el backend suma las mermas del responsable. El roto por máquina no cuenta. Si la suma supera la tolerancia, llega una notificación a quien tenga lectura en el módulo Bitácora de Venta.

```
POST   /api/v1/tabulador-botellones-rotos
GET    /api/v1/tabulador-botellones-rotos
GET    /api/v1/tabulador-botellones-rotos/{id}
PUT    /api/v1/tabulador-botellones-rotos/{id}
DELETE /api/v1/tabulador-botellones-rotos/{id}
```

Request de alta y edición. El `PUT` reemplaza la fila.

```json
{
  "idEmpleado": "11111111-1111-1111-1111-111111111111",
  "periodo": "SEMANAL",
  "tolerancia": 5.00,
  "idProducto": "8d798c8d-7982-4a8d-9d86-475d83c0c5a2",
  "idUnidadMedida": "22222222-2222-2222-2222-222222222222"
}
```

| Campo | Tipo | Obligatorio |
| --- | --- | --- |
| `idEmpleado` | UUID | Sí. Empleado activo |
| `periodo` | string | Sí. Único valor: `SEMANAL` |
| `tolerancia` | number | Sí. Mayor que 0, máximo dos decimales |
| `idProducto` | UUID | Sí. Producto comercial activo |
| `idUnidadMedida` | UUID | Sí. Se muestra en el aviso. No convierte la cantidad de la merma |

`POST` responde `201`. `PUT` y `DELETE` responden `200`.

```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "empleadoId": "11111111-1111-1111-1111-111111111111",
  "empleadoNombre": "Ana López",
  "periodo": "SEMANAL",
  "tolerancia": 5.00,
  "productoId": "8d798c8d-7982-4a8d-9d86-475d83c0c5a2",
  "productoNombre": "Garrafón",
  "unidadMedidaId": "22222222-2222-2222-2222-222222222222",
  "unidadMedidaNombre": "Pieza",
  "createdAt": "2026-09-29T18:00:00",
  "updatedAt": "2026-09-29T18:00:00",
  "enabled": true
}
```

El listado va de la más reciente a la más antigua y no incluye filas eliminadas. Filtros: `id`, `empleado`, `idEmpleado`, `producto`, `idProducto`, `enabled`.

Un empleado y un producto solo pueden tener un tabulador activo. Al borrar, se puede volver a crear.

La campana sigue siendo `GET /notificaciones`. El tipo nuevo es `BOTELLON_ROTO_SOBRE_TOLERANCIA`. El `mensaje` nombra al empleado, el producto, los rotos de la semana, la tolerancia, la unidad y el rango de fechas. `productoId` es el producto comercial. `periodoInicio` y `periodoFin` son el lunes 00:00 y el domingo 23:59:59 de esa semana. Mientras ese aviso no se lea, no se genera otro del mismo empleado, producto y semana.

| Regla | Respuesta |
| --- | --- |
| Falta un campo o la tolerancia no es mayor que 0 o tiene más de dos decimales | `400`. Mapa de campo a mensaje |
| Empleado, producto, unidad o fila inexistente o inactiva | `409` |
| Ese empleado ya tiene tabulador para ese producto | `409`. `message`: `Ese empleado ya tiene un tabulador para ese producto` |

## 4. Tabla de amonestación

Igual que la tabla de comisiones, sin venta mínima. Cada producto Tancol solo puede tener una fila activa. El descuento es un monto a dos decimales menor o igual a cero, para mostrarlo como `-$00.00`. A la derecha del campo va `textoDescuento`.

```
POST   /api/v1/tablas-amonestaciones
GET    /api/v1/tablas-amonestaciones
GET    /api/v1/tablas-amonestaciones/{id}
PUT    /api/v1/tablas-amonestaciones/{id}
DELETE /api/v1/tablas-amonestaciones/{id}
```

```json
{
  "cantidadDesde": 1,
  "cantidadHasta": 5,
  "idProducto": "8d798c8d-7982-4a8d-9d86-475d83c0c5a2",
  "descuento": -12.50
}
```

| Campo | Tipo | Obligatorio |
| --- | --- | --- |
| `cantidadDesde` | entero | Sí. `>= 1`. En pantalla es De |
| `cantidadHasta` | entero | Sí. `>= cantidadDesde`. En pantalla es A |
| `idProducto` | UUID | Sí. Producto comercial activo |
| `descuento` | number | Sí. `<= 0` y máximo dos decimales. `0.00` es válido |

`POST` responde `201`. `PUT` responde `200`.

```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "cantidadDesde": 1,
  "cantidadHasta": 5,
  "productoId": "8d798c8d-7982-4a8d-9d86-475d83c0c5a2",
  "productoNombre": "Garrafón",
  "productoAlias": "GAR",
  "descuento": -12.50,
  "textoDescuento": "Por cada Garrafón roto",
  "createdAt": "2026-09-29T18:10:00",
  "updatedAt": "2026-09-29T18:10:00",
  "enabled": true
}
```

El combo de producto sale de:

```
GET /api/v1/tablas-amonestaciones/productos-disponibles
```

Al editar, pasar el id de la fila para que su producto siga en la lista:

```
GET /api/v1/tablas-amonestaciones/productos-disponibles?tablaId=3fa85f64-5717-4562-b3fc-2c963f66afa6
```

```json
[
  {
    "idProducto": "8d798c8d-7982-4a8d-9d86-475d83c0c5a2",
    "nombre": "Garrafón",
    "alias": "GAR"
  }
]
```

Al cambiar el producto en el formulario, el texto de al lado puede armarse como `Por cada {nombre} roto` hasta que responda el guardado. Después usar `textoDescuento`.

El listado no incluye filas eliminadas. Filtros: `id`, `producto`, `idProducto`, `enabled`. `DELETE` responde `200` y el producto vuelve al combo.

| Regla | Respuesta |
| --- | --- |
| Falta un campo, De menor que 1, descuento positivo o con más de dos decimales | `400`. Mapa de campo a mensaje |
| A menor que De | `400`. `{ "rangoValido": "A debe ser igual o mayor que De" }` |
| El producto no existe, está inactivo o la fila no existe | `409` |
| El producto ya tiene una tabla activa | `409`. `message`: `Ese producto ya tiene una tabla de amonestación` |
