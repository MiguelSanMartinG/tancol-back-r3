# Integración: KPIs de insumos

Dos listas del dashboard. El periodo es obligatorio y las fechas son inclusivas (`YYYY-MM-DD`).

Requiere JWT.

```
Authorization: Bearer <token>
```

Base local: `/api/v1`. Producción: `https://back.purificadoratancol.com/api/v1`.

`limit` es opcional. Si no se envía, o es menor a 1, el backend usa 10. El máximo es 100.

Si no hay datos, la lista llega vacía.

También van dentro de `GET /dashboard/kpis/official`, en `topDepartamentosInsumos` y `topInsumosComprados`. Ese endpoint usa el mismo `limit`.

## Top de departamentos

Los 10 departamentos que más insumos solicitaron en órdenes internas, y de cada uno los 3 insumos con mayor cantidad.

```
GET /api/v1/dashboard/kpis/top-departamentos-insumos?fechaInicio=2026-09-01&fechaFin=2026-09-30
```

Producción:

```
GET https://back.purificadoratancol.com/api/v1/dashboard/kpis/top-departamentos-insumos?fechaInicio=2026-09-01&fechaFin=2026-09-30
```

| Parámetro | Obligatorio | Uso |
| --- | --- | --- |
| `fechaInicio` | Sí | Inicio del periodo |
| `fechaFin` | Sí | Fin del periodo |
| `limit` | No | Cuántos departamentos. Por defecto 10 |

El departamento es el campo `uso` de la orden interna. Un uso vacío se muestra como `Sin uso`. No entran órdenes eliminadas ni en estado `BORRADOR`.

`cantidadTotal` suma todo lo pedido por ese departamento y define el orden. Los 3 productos se ordenan por su propia cantidad. El mismo insumo en la misma unidad se suma. Si cambia la unidad, cuenta como otra línea.

`unidad` sale de la partida. Si la partida no trae unidad, se usa la unidad por defecto del insumo.

### Response `200`

```json
[
  {
    "departamento": "Limpieza",
    "cantidadTotal": 690,
    "productos": [
      {
        "idProducto": "8d798c8d-7982-4a8d-9d86-475d83c0c5a2",
        "producto": "Cloralex",
        "cantidad": 600,
        "unidad": "L"
      },
      {
        "idProducto": "1b4e28ba-2fa1-11d2-883f-0016d3cca427",
        "producto": "Papel de baño",
        "cantidad": 50,
        "unidad": "paquetes"
      },
      {
        "idProducto": "6ba7b810-9dad-11d1-80b4-00c04fd430c8",
        "producto": "Escobas",
        "cantidad": 40,
        "unidad": "pz"
      }
    ]
  },
  {
    "departamento": "TI",
    "cantidadTotal": 115,
    "productos": [
      {
        "idProducto": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
        "producto": "Cable TPU",
        "cantidad": 100,
        "unidad": "paquetes"
      },
      {
        "idProducto": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
        "producto": "Hojas carta",
        "cantidad": 10,
        "unidad": "pz"
      },
      {
        "idProducto": "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d",
        "producto": "Mouse Logitec",
        "cantidad": 5,
        "unidad": "pz"
      }
    ]
  }
]
```

| Campo | Uso en pantalla |
| --- | --- |
| `departamento` | Encabezado. Ejemplo: TI, Producción, Limpieza |
| `cantidadTotal` | Total solicitado por el departamento |
| `productos` | Los 3 insumos que más pidió |
| `productos[].producto` | Nombre del insumo |
| `productos[].cantidad` | Cantidad solicitada |
| `productos[].unidad` | Unidad. Ejemplo: paquetes, pz, L |
| `productos[].idProducto` | Para abrir el insumo. No va como texto |

Texto de cada producto: `Cable TPU: 100 paquetes`.

## Top de insumos comprados

Los 10 insumos con más cantidad en órdenes de compra del periodo.

```
GET /api/v1/dashboard/kpis/top-insumos-comprados?fechaInicio=2026-09-01&fechaFin=2026-09-30
```

Producción:

```
GET https://back.purificadoratancol.com/api/v1/dashboard/kpis/top-insumos-comprados?fechaInicio=2026-09-01&fechaFin=2026-09-30
```

| Parámetro | Obligatorio | Uso |
| --- | --- | --- |
| `fechaInicio` | Sí | Inicio del periodo, por fecha de la orden |
| `fechaFin` | Sí | Fin del periodo |
| `limit` | No | Cuántos insumos. Por defecto 10 |

La fecha es `fecha_solicitud` de la orden de compra. No entran órdenes eliminadas, en `BORRADOR` ni `CANCELADA`. Sí entran `PENDIENTE`, `AUTORIZADO` y `COMPLETADA`.

El mismo insumo en la misma unidad se suma. Si el insumo se compró en otra unidad, aparece como otra línea y compite por su propia cantidad.

### Response `200`

```json
[
  {
    "idProducto": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "producto": "Cable TPU",
    "cantidad": 100,
    "unidad": "paquetes"
  },
  {
    "idProducto": "8d798c8d-7982-4a8d-9d86-475d83c0c5a2",
    "producto": "Filtro ABC",
    "cantidad": 50,
    "unidad": "pz"
  }
]
```

| Campo | Uso en pantalla |
| --- | --- |
| `producto` | Nombre del insumo |
| `cantidad` | Cantidad comprada |
| `unidad` | Unidad de la partida, o la unidad por defecto del insumo |
| `idProducto` | Para abrir el insumo |

Texto de cada fila: `Cable TPU: 100 paquetes`.
