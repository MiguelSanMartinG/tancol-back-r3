# Integración: litros por minuto en bitácora de medición

Campo nuevo al registrar una medición. El front lo captura en el alta y lo muestra en el listado. El backend lo guarda tal como llega; no lo calcula.

Requiere JWT.

```
Authorization: Bearer <token>
Content-Type: application/json
```

Base local: `/api/v1`. Producción: `https://back.purificadoratancol.com/api/v1`.

## Qué hace el front

1. En el formulario de nueva medición, agregar un número opcional `litrosMinuto`.
2. Enviarlo en el `POST` junto con el resto de la medición.
3. En el listado y en el detalle, leer `litrosMinuto` de cada registro.
4. Las mediciones anteriores no traen el dato: el valor llega en `null`. Mostrar vacío, no cero.

## Alta

```
POST /api/v1/bitacora-medicion
```

Responde `201`.

Request:

```json
{
  "idMedidor": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "medicionActual": 120.5,
  "comentario": "turno mañana",
  "liquidado": 10,
  "quedadoEnPipa": 2,
  "litrosMinuto": 18.5
}
```

| Campo | Tipo | Obligatorio |
| --- | --- | --- |
| `idMedidor` | UUID | Sí |
| `medicionActual` | number | Sí. Debe ser mayor que 0 |
| `comentario` | string | No |
| `liquidado` | number | No |
| `quedadoEnPipa` | number | No |
| `litrosMinuto` | number | No. Hasta 2 decimales. Si no se envía, se guarda `null` |

## Consulta

```
GET /api/v1/bitacora-medicion
```

Responde `200` con la lista. Cada elemento incluye el campo nuevo:

```json
{
  "id": "8d798c8d-7982-4a8d-9d86-475d83c0c5a2",
  "medicionInicial": 100.0,
  "medicionFinal": 120.5,
  "consumo": 20.5,
  "liquidado": 10,
  "quedadoEnPipa": 2,
  "litrosMinuto": 18.5,
  "diferencia": 8.5,
  "medidoHoy": true,
  "usuario": "ana.lopez",
  "comentario": "turno mañana",
  "cratedAt": "2026-10-09T16:00:00",
  "medidor": {}
}
```

| Campo | Tipo | Notas |
| --- | --- | --- |
| `litrosMinuto` | number o `null` | Litros por minuto capturados en esa medición |

El nombre en JSON es `litrosMinuto`. En base de datos la columna es `litros_minuto`.
