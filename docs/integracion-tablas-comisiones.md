# Integración: Tablas de comisiones

Pantalla de configuración para la nómina. Cada fila guarda un rango de cantidad y la comisión de un producto comercial (Producto Tancol). La tabla es independiente: la única relación es con productos comerciales. Todavía no calcula la nómina.

Requiere JWT.

```
Authorization: Bearer <token>
Content-Type: application/json
```

Base local: `/api/v1`. Producción: `https://back.purificadoratancol.com/api/v1`.

## Qué hace el front

1. Listar las tablas con `GET /tablas-comisiones`.
2. El combo de producto sale de `GET /tablas-comisiones/productos-disponibles`. Los productos ya usados no vienen en esa lista.
3. Al escribir venta mínima, proponer ese mismo número en De. El usuario puede cambiarlo. De sigue siendo un entero de mínimo 1.
4. A debe ser igual o mayor que De.
5. La comisión es un monto a dos decimales y puede iniciar en `$0.00`. A la derecha del campo mostrar `textoComision`, por ejemplo `Por cada Garrafón vendido`.
6. Guardar con `POST` o `PUT`. Borrar con `DELETE`. Al borrar, el producto vuelve a aparecer en el combo.

Un producto comercial solo puede tener una tabla activa.

## Pantalla

| Control | Campo | Notas |
| --- | --- | --- |
| Venta mínima | `ventaMinima` | Entero. Mínimo 1 |
| De | `cantidadDesde` | Entero. Mínimo 1. Valor inicial sugerido: lo escrito en venta mínima |
| A | `cantidadHasta` | Entero. Igual o mayor que De |
| Producto Tancol | `idProducto` | UUID del producto comercial. Ocultar los ya seleccionados |
| Comisión | `comision` | Número a dos decimales. Mínimo `0.00`. Puede ser `0.00` |
| Texto | `textoComision` | Solo lectura. Va después del campo de comisión. Lo arma el backend: `Por cada {nombre} vendido` |

El `PUT` reemplaza la fila completa. Hay que reenviar los cinco campos.

## Alta y edición

```
POST /api/v1/tablas-comisiones
PUT  /api/v1/tablas-comisiones/{id}
```

Request:

```json
{
  "ventaMinima": 10,
  "cantidadDesde": 10,
  "cantidadHasta": 25,
  "idProducto": "8d798c8d-7982-4a8d-9d86-475d83c0c5a2",
  "comision": 0.00
}
```

| Campo | Tipo | Obligatorio |
| --- | --- | --- |
| `ventaMinima` | entero | Sí. `>= 1` |
| `cantidadDesde` | entero | Sí. `>= 1`. En pantalla es De |
| `cantidadHasta` | entero | Sí. `>= cantidadDesde`. En pantalla es A |
| `idProducto` | UUID | Sí. Producto comercial activo |
| `comision` | number | Sí. `>= 0` y máximo dos decimales |

`POST` responde `201`. `PUT` responde `200`. El cuerpo es la tabla:

```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "ventaMinima": 10,
  "cantidadDesde": 10,
  "cantidadHasta": 25,
  "productoId": "8d798c8d-7982-4a8d-9d86-475d83c0c5a2",
  "productoNombre": "Garrafón",
  "productoAlias": "GAR",
  "comision": 0.00,
  "textoComision": "Por cada Garrafón vendido",
  "createdAt": "2026-09-29T10:40:00",
  "updatedAt": "2026-09-29T10:40:00",
  "enabled": true
}
```

| Campo | Uso en pantalla |
| --- | --- |
| `id` | Identificador de la fila. Se usa en edición, borrado y en el combo al editar |
| `cantidadDesde` | Columna De |
| `cantidadHasta` | Columna A |
| `productoNombre` | Nombre en la tabla y en el texto de la comisión |
| `comision` | Mostrar como `$0.00` |
| `textoComision` | Texto a la derecha del campo comisión |

## Consulta

```
GET /api/v1/tablas-comisiones
GET /api/v1/tablas-comisiones/{id}
```

`GET /tablas-comisiones` devuelve un arreglo, de la más reciente a la más antigua. No incluye filas eliminadas.

Filtros opcionales: `id`, `producto`, `idProducto`, `enabled`. Cualquier otro query param responde error.

```
GET /api/v1/tablas-comisiones?idProducto=8d798c8d-7982-4a8d-9d86-475d83c0c5a2
```

## Productos disponibles

Combo de Producto Tancol. Solo productos comerciales activos que todavía no tienen tabla.

```
GET /api/v1/tablas-comisiones/productos-disponibles
```

Al editar, pasar el id de la fila para que su producto siga en la lista:

```
GET /api/v1/tablas-comisiones/productos-disponibles?tablaId=3fa85f64-5717-4562-b3fc-2c963f66afa6
```

Response `200`:

```json
[
  {
    "idProducto": "8d798c8d-7982-4a8d-9d86-475d83c0c5a2",
    "nombre": "Garrafón",
    "alias": "GAR"
  }
]
```

`idProducto` es el valor que se envía en el alta y en la edición. Al cambiar la selección, el texto de al lado puede armarse en el front como `Por cada {nombre} vendido` hasta que responda el guardado. Después usar `textoComision`.

## Borrado

```
DELETE /api/v1/tablas-comisiones/{id}
```

Responde `200` con la misma forma del alta. La fila deja de salir en el listado y el producto vuelve a `productos-disponibles`.

## Validaciones

| Regla | Respuesta |
| --- | --- |
| Falta un campo, venta mínima o De menor que 1, comisión negativa o con más de dos decimales | `400`. Mapa de campo a mensaje, por ejemplo `{ "ventaMinima": "La venta mínima debe ser un entero mayor o igual a 1" }` |
| A menor que De | `400`. `{ "rangoValido": "A debe ser igual o mayor que De" }` |
| El producto no existe, está inactivo o la fila no existe | `409`. `{ "status": 409, "message": "No se encontró el producto", "requestURI": "/api/v1/tablas-comisiones", "timestamp": "2026-09-29T10:40:00" }` |
| El producto ya tiene una tabla activa | `409`. `message`: `Ese producto ya tiene una tabla de comisiones` |
| Query param no reconocido en el listado | `409`. `message`: `Invalid filter: {param}` |
