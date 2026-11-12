# Integración: reporte general no factura autoservicio

Base: `/api/v1`. Misma autenticación JWT que el resto de la API.

Es el mismo reporte que `POST /reportes/general/rg-nofactura`. La diferencia es el tipo de ruta: todas las columnas filtran solo `AUTO`.

Solo entran liquidaciones `FINALIZADA`, con partidas no canceladas y `factura = false`, entre `fechaInicio` y `fechaFin` (ambas inclusive). El importe se agrupa por el chofer de la liquidación.

Las fechas van en ISO-8601 local, sin zona: `2026-10-01T00:00:00`.

## Consultar

`POST /reportes/general/rg-nofactura-autoservicio`

```json
{
  "fechaInicio": "2026-10-01T00:00:00",
  "fechaFin": "2026-10-31T23:59:59"
}
```

`200` con el reporte. Si no hay filas, `columnas` y `filas` llegan vacíos, `totalesPorColumna` llega `{}` y `granTotal` es `0`. En ese caso no vienen las claves de columna: solo aparecen cuando hay al menos un empleado.

## Respuesta

`columnas` trae las claves en este orden. `filas[].columnas` y `totalesPorColumna` usan las mismas claves. Los importes son dinero.

| Clave |
| --- |
| `e20` |
| `des_liq20` |
| `des` |
| `pg` |
| `bpsc` |
| `exp` |
| `agua_purif` |
| `agua_pot` |
| `agua_dm` |
| `h5kg` |
| `h3kg` |
| `c350` |
| `c500` |
| `c1000` |
| `c1500` |
| `desc_hielo` |
| `desc_h5kg` |
| `e11` |
| `liq11` |
| `liq10` |

`filas` va ordenado por `nombreEmpleado`, sin distinguir mayúsculas. `totalFila` es la suma de las columnas de ese empleado. `totalesPorColumna` suma cada columna. `granTotal` es la suma de `totalesPorColumna`.

`nombreEmpleado` es nombre y apellidos. Si el empleado no está o no tiene nombre, llega el `empleadoId` como texto.

```json
{
  "columnas": ["e20", "des_liq20", "des", "pg", "bpsc", "exp", "agua_purif", "agua_pot", "agua_dm", "h5kg", "h3kg", "c350", "c500", "c1000", "c1500", "desc_hielo", "desc_h5kg", "e11", "liq11", "liq10"],
  "filas": [
    {
      "empleadoId": "uuid",
      "nombreEmpleado": "Nombre Apellido Apellido",
      "columnas": {
        "e20": 0,
        "des_liq20": 0,
        "des": 0,
        "pg": 120.50,
        "bpsc": 0,
        "exp": 0,
        "agua_purif": 0,
        "agua_pot": 0,
        "agua_dm": 0,
        "h5kg": 0,
        "h3kg": 0,
        "c350": 0,
        "c500": 0,
        "c1000": 0,
        "c1500": 0,
        "desc_hielo": 0,
        "desc_h5kg": 0,
        "e11": 0,
        "liq11": 0,
        "liq10": 0
      },
      "totalFila": 120.50
    }
  ],
  "totalesPorColumna": {
    "e20": 0,
    "des_liq20": 0,
    "des": 0,
    "pg": 120.50,
    "bpsc": 0,
    "exp": 0,
    "agua_purif": 0,
    "agua_pot": 0,
    "agua_dm": 0,
    "h5kg": 0,
    "h3kg": 0,
    "c350": 0,
    "c500": 0,
    "c1000": 0,
    "c1500": 0,
    "desc_hielo": 0,
    "desc_h5kg": 0,
    "e11": 0,
    "liq11": 0,
    "liq10": 0
  },
  "granTotal": 120.50
}
```

## Errores

`400` si falta `fechaInicio` o `fechaFin`, o si `fechaFin` es anterior a `fechaInicio`.

`400` si el body no es JSON válido. En ese caso el cuerpo sigue el formato de error de la API: `status`, `message`, `requestURI`, `timestamp`.

`401` sin JWT.
