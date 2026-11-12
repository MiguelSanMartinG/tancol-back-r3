# Integración: Reporte de salida de material

Lista las salidas de material a partir de las partidas de órdenes internas. Una fila por partida.

## Endpoint

```
POST /api/v1/reportes/salida-material
```

Producción:

```
POST https://back.purificadoratancol.com/api/v1/reportes/salida-material
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
| Folio | `folio` de la orden |
| Fecha | `fechaElaboracion` de la orden |
| Área | Campo `uso` de la orden |
| Estado | `estadoOrdenInterna` de la orden |
| Empleado que entrega | Quien envía la orden (nombre y apellidos) |
| Empleado que recibe | Quien recibe la orden (nombre y apellidos) |
| Almacén | Nombre del almacén de la partida |
| Insumo | Nombre del producto básico |
| Alias | Alias del producto básico |
| Unidad de medida | Nombre de la unidad de la partida |
| Cantidad | `cantidad` de la partida |
| Comentario de partida | `comentario` de la partida |
| Comentario de la orden | `descripcion` de la orden |

Las órdenes eliminadas y las que están en estado `BORRADOR` no entran. Sí entran `EN_USO`, `EN_ALMACEN`, `SIN_RETORNO`, `MALA_CONDICION` y las que no tienen estado.

Un `uso` vacío se muestra como `Sin uso`. Un insumo, almacén, unidad o empleado vacío se muestra como `Sin insumo`, `Sin almacén`, `Sin unidad` o `Sin empleado`.

## Request

Todos los filtros son opcionales. Una lista ausente, `null` o `[]` significa “todos”.

| Campo | Tipo | Uso |
| --- | --- | --- |
| `fechaInicio` | fecha-hora ISO-8601 | Inclusive. Si solo viene este, filtra desde esa fecha. |
| `fechaFin` | fecha-hora ISO-8601 | Inclusive. Si solo viene este, filtra hasta esa fecha. |
| `areas` | `string[]` | Área (`uso`). Coincidencia exacta, sin distinguir mayúsculas. `Sin uso` incluye el uso vacío. |
| `idEmpleados` | `UUID[]` | Empleado receptor. |
| `idInsumos` | `UUID[]` | Producto básico. |
| `idAlmacenes` | `UUID[]` | Almacén de la partida. |

También acepta un solo valor en lugar de arreglo en `areas`, `idEmpleados`, `idInsumos` e `idAlmacenes`.

Ejemplo:

```json
{
  "fechaInicio": "2026-09-01T00:00:00",
  "fechaFin": "2026-09-30T23:59:59",
  "areas": ["Producción"],
  "idEmpleados": ["3fa85f64-5717-4562-b3fc-2c963f66afa6"],
  "idInsumos": ["7c9e6679-7425-40de-944b-e07fc1f90ae7"],
  "idAlmacenes": ["8d798c8d-7982-4a8d-9d86-475d83c0c5a2"]
}
```

Sin filtros, el cuerpo puede ir vacío:

```json
{}
```

## Response `200`

```json
{
  "filas": [
    {
      "idPartida": "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d",
      "idOrdenInterna": "1b4e28ba-2fa1-11d2-883f-0016d3cca427",
      "folio": "OI-1042",
      "fecha": "2026-09-12T10:30:00",
      "area": "Producción",
      "estado": "EN_USO",
      "empleadoEntrega": "Luis Pérez",
      "empleadoRecibe": "Ana López García",
      "idAlmacen": "8d798c8d-7982-4a8d-9d86-475d83c0c5a2",
      "almacen": "Almacén general",
      "idInsumo": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
      "insumo": "Plumas",
      "alias": "PLUMA",
      "unidadMedida": "Pieza",
      "cantidad": 144,
      "comentarioPartida": "Entrega de quincena",
      "comentarioOrden": "Material de oficina"
    }
  ],
  "totalesPorInsumo": [
    {
      "insumo": "Plumas",
      "unidadMedida": "Pieza",
      "cantidad": 288
    }
  ],
  "totalFilas": 1
}
```

### Columnas de cada fila

Van ordenadas por fecha descendente, luego folio e insumo.

| Campo | Columna |
| --- | --- |
| `folio` | Folio |
| `fecha` | Fecha |
| `area` | Área |
| `estado` | Estado |
| `empleadoEntrega` | Entrega |
| `empleadoRecibe` | Recibe |
| `almacen` | Almacén |
| `insumo` | Insumo |
| `alias` | Alias |
| `unidadMedida` | Unidad |
| `cantidad` | Cantidad |
| `comentarioPartida` | Comentario de partida |
| `comentarioOrden` | Comentario de la orden |

`idPartida`, `idOrdenInterna`, `idAlmacen` e `idInsumo` identifican la partida, la orden, el almacén y el insumo.

### Totalizadores

Se calculan con las mismas filas filtradas. `totalesPorInsumo` suma la cantidad por insumo y unidad de medida, para no mezclar piezas con litros. Van ordenados por insumo y luego por unidad.

`totalFilas` es el número de partidas.

Si no hay resultados, `filas` y `totalesPorInsumo` llegan vacías y `totalFilas` es `0`.

## Ejemplo de llamada

```http
POST /api/v1/reportes/salida-material HTTP/1.1
Host: back.purificadoratancol.com
Authorization: Bearer <token>
Content-Type: application/json

{
  "fechaInicio": "2026-09-01T00:00:00",
  "fechaFin": "2026-09-30T23:59:59"
}
```
