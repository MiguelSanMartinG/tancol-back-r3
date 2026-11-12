# Integración: Reporte de consumo por área y producto

Consulta el consumo de insumos a partir de las partidas de órdenes internas.

## Endpoint

```
POST /api/v1/reportes/consumo-area-producto
```

Producción:

```
POST https://back.purificadoratancol.com/api/v1/reportes/consumo-area-producto
```

Requiere JWT.

```
Authorization: Bearer <token>
Content-Type: application/json
```

## Qué devuelve

Cada fila es una partida de orden interna.

| Campo de la fila | Origen |
| --- | --- |
| Fecha | `fechaElaboracion` de la orden |
| Empleado | Quien recibe la orden (nombre y apellidos) |
| Insumo | Nombre del producto básico |
| Cantidad solicitada | `cantidad` de la partida |
| Comentario de partida | `comentario` de la partida |
| Área | Campo `uso` de la orden |

Las órdenes eliminadas y las que están en estado `BORRADOR` no entran.

Un `uso` vacío se muestra como `Sin uso`.

## Request

Todos los filtros son opcionales. Una lista ausente, `null` o `[]` significa “todos”.

| Campo | Tipo | Uso |
| --- | --- | --- |
| `fechaInicio` | fecha-hora ISO-8601 | Inclusive. Si solo viene este, filtra desde esa fecha. |
| `fechaFin` | fecha-hora ISO-8601 | Inclusive. Si solo viene este, filtra hasta esa fecha. |
| `idEmpleados` | `UUID[]` | Empleado receptor. |
| `idInsumos` | `UUID[]` | Producto básico. |
| `idCategorias` | `UUID[]` | Categoría del insumo. |
| `alias` | `string[]` | Alias del insumo, coincidencia exacta. |

También acepta un solo valor en lugar de arreglo en `idEmpleados`, `idInsumos`, `idCategorias` y `alias`.

Ejemplo:

```json
{
  "fechaInicio": "2026-09-01T00:00:00",
  "fechaFin": "2026-09-30T23:59:59",
  "idEmpleados": ["3fa85f64-5717-4562-b3fc-2c963f66afa6"],
  "idInsumos": ["7c9e6679-7425-40de-944b-e07fc1f90ae7"],
  "idCategorias": [],
  "alias": ["PLUMA"]
}
```

Sin filtros, el cuerpo puede ir vacío:

```json
{}
```

## Response `200`

```json
{
  "grupos": [
    {
      "area": "Producción",
      "insumo": "Plumas",
      "cantidad": 288,
      "filas": [
        {
          "fecha": "2026-09-12T10:30:00",
          "empleado": "Ana López García",
          "insumo": "Plumas",
          "cantidadSolicitada": 144,
          "comentarioPartida": "Entrega de quincena"
        }
      ]
    }
  ],
  "totalesPorInsumo": [
    {
      "insumo": "Plumas",
      "cantidad": 288
    }
  ],
  "totalesPorEmpleado": [
    {
      "empleado": "Ana López García",
      "cantidad": 144
    }
  ]
}
```

### Grupos

Un grupo por cada par área + insumo. Van ordenados por área y luego por insumo.

| Campo | Uso en pantalla |
| --- | --- |
| `area` | Encabezado del área. Ejemplo: Producción. |
| `insumo` | Producto del grupo. |
| `cantidad` | Suma de las cantidades solicitadas de ese par. Con `area` e `insumo` se arma el texto “Producción consumió 288 plumas”. |
| `filas` | Detalle, ordenado por fecha. |

### Columnas de cada fila

| Campo | Columna |
| --- | --- |
| `fecha` | Fecha |
| `empleado` | Empleado |
| `insumo` | Insumo |
| `cantidadSolicitada` | Cantidad solicitada |
| `comentarioPartida` | Comentario de partida |

### Totalizadores

Van al final y se calculan con las mismas filas filtradas.

| Lista | Agrupa por |
| --- | --- |
| `totalesPorInsumo` | Insumo |
| `totalesPorEmpleado` | Empleado |

Si no hay resultados, las tres listas llegan vacías.

## Ejemplo de llamada

```http
POST /api/v1/reportes/consumo-area-producto HTTP/1.1
Host: back.purificadoratancol.com
Authorization: Bearer <token>
Content-Type: application/json

{
  "fechaInicio": "2026-09-01T00:00:00",
  "fechaFin": "2026-09-30T23:59:59"
}
```
