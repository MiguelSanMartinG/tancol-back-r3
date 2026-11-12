# Integración: Reporte de bitácoras de ventas

Consulta las partidas de venta de cada trayecto de bitácora. Cada fila es un producto del trayecto.

## Endpoint

```
POST /api/v1/reportes/bitacoras-ventas
```

Producción:

```
POST https://back.purificadoratancol.com/api/v1/reportes/bitacoras-ventas
```

Requiere JWT.

```
Authorization: Bearer <token>
Content-Type: application/json
```

## Qué devuelve

Una lista plana. Un trayecto con varios productos aparece una vez por producto.

| Campo | Origen |
| --- | --- |
| `idBitacora`, `folio`, `fecha`, `estatusBitacora` | Bitácora |
| `idTrayecto`, `tipoProceso`, `chofer`, `vehiculo`, `fechaFirma`, `estadoVentaRuta`, `mandadoALiquidacion`, `comentario` | Trayecto |
| `idVenta`, `nombreProducto`, cantidades, `huecos`, `rotos` | Partida de producto del trayecto |
| `ayudantes` | Comisiones del trayecto, nombres separados por coma |
| `fueraEspecificacion`, `cantidadFueraEspecificacion` | Productos fuera de especificación de esa partida |

`rotos` es la suma de todas las mermas de la partida, incluidas las marcadas como rotas por máquina.

`estatusBitacora`: `BORRADOR`, `GUARDADA`.

`estadoVentaRuta`: `PENDIENTE`, `FIRMADO`.

`tipoProceso`: `MANUAL`, `AUTOMATICO`, `MANUAL_1`, `MANUAL_2`, `TORRES`, `SIN_PROCESO`, `AUTOSERVICIO`.

## Request

Todos los filtros son opcionales. Una lista ausente, `null` o `[]` significa “todos”. Un booleano ausente o `null` no filtra.

| Campo | Tipo | Uso |
| --- | --- | --- |
| `fechaInicio` | fecha-hora ISO-8601 | Inclusive, por fecha de la bitácora. |
| `fechaFin` | fecha-hora ISO-8601 | Inclusive, por fecha de la bitácora. |
| `folio` | string | Contiene, sin distinguir mayúsculas. |
| `idRutas` | `UUID[]` | Ruta del trayecto. |
| `procesos` | `TipoProceso[]` | Proceso del trayecto. |
| `idChoferes` | `UUID[]` | Chofer del trayecto. |
| `idAyudantes` | `UUID[]` | Ayudante de la partida o empleado asignado a la ruta. |
| `idProductos` | `UUID[]` | Producto comercial de la partida. |
| `idVehiculos` | `UUID[]` | Vehículo del trayecto. |
| `fechaFirma` | fecha-hora ISO-8601 | Coincide el día de la firma del trayecto. |
| `rotos` | boolean | `true`: la partida tiene rotos. `false`: no tiene rotos. |
| `rotoPorMaquina` | boolean | `true`: la partida tiene al menos un roto por máquina. `false`: no tiene rotos por máquina. |
| `fueraEspecificacion` | boolean | `true`: la partida tiene fuera de especificación. `false`: no tiene. |

Los arreglos también aceptan un solo valor. Alias aceptados:

| Campo | Alias |
| --- | --- |
| `idRutas` | `idRuta`, `rutas` |
| `procesos` | `tipoProceso`, `tipoProcesos`, `proceso` |
| `idChoferes` | `idChofer`, `choferes` |
| `idAyudantes` | `idAyudante`, `ayudantes` |
| `idProductos` | `idProducto`, `productos` |
| `idVehiculos` | `idVehiculo`, `vehiculos` |
| `rotoPorMaquina` | `rotosPorMaquina`, `rotasPorMaquina`, `roto_por_maquina` |

### Rotos por máquina

El checkbox del reporte envía `rotoPorMaquina: true` para quedarse solo con las partidas que tienen merma marcada como rota por máquina.

`rotos` y `rotoPorMaquina` se combinan. Para ver solo rotos de máquina:

```json
{
  "fechaInicio": "2026-09-01T00:00:00",
  "fechaFin": "2026-09-30T23:59:59",
  "rotoPorMaquina": true
}
```

Si el checkbox no está marcado, no envíes el campo. Enviar `false` excluye las partidas que sí tienen roto por máquina.

## Ejemplo con el resto de filtros

```json
{
  "fechaInicio": "2026-09-01T00:00:00",
  "fechaFin": "2026-09-30T23:59:59",
  "folio": "BV-12",
  "idRutas": ["3fa85f64-5717-4562-b3fc-2c963f66afa6"],
  "procesos": ["MANUAL"],
  "idChoferes": ["7c9e6679-7425-40de-944b-e07fc1f90ae7"],
  "idAyudantes": [],
  "idProductos": [],
  "idVehiculos": [],
  "fechaFirma": "2026-09-15T00:00:00",
  "rotos": true,
  "rotoPorMaquina": true,
  "fueraEspecificacion": null
}
```

Sin filtros, el cuerpo puede ir vacío:

```json
{}
```

## Response `200`

Arreglo de filas, ordenado por fecha de bitácora descendente, folio, ruta y producto.

```json
[
  {
    "idBitacora": "8d798c8d-7982-4a8d-9d86-475d83c0c5a2",
    "idTrayecto": "1b4e28ba-2fa1-11d2-883f-0016d3cca427",
    "idVenta": "6ba7b810-9dad-11d1-80b4-00c04fd430c8",
    "folio": "BV-120",
    "fecha": "2026-09-15",
    "estatusBitacora": "GUARDADA",
    "nombreRuta": "Ruta Centro",
    "tipoProceso": "MANUAL",
    "chofer": "Luis Pérez Hernández",
    "ayudantes": "Ana López García, Juan Díaz Ruiz",
    "nombreProducto": "Garrafón 20 L",
    "vehiculo": "Unidad 4 - ABC-123",
    "fechaFirma": "2026-09-15T18:40:00",
    "cantidadSalida": 40,
    "cantidadLlegada": 8,
    "cantidadVendida": 30,
    "huecos": 1,
    "rotos": 2,
    "fueraEspecificacion": "Golpeado (1)",
    "cantidadFueraEspecificacion": 1,
    "estadoVentaRuta": "FIRMADO",
    "mandadoALiquidacion": true,
    "comentario": "Roto en llenado"
  }
]
```

### Columnas

| Campo | Columna |
| --- | --- |
| `folio` | Folio |
| `fecha` | Fecha |
| `estatusBitacora` | Estatus de la bitácora |
| `nombreRuta` | Ruta |
| `tipoProceso` | Proceso |
| `chofer` | Chofer |
| `ayudantes` | Ayudantes |
| `nombreProducto` | Producto |
| `vehiculo` | Vehículo |
| `fechaFirma` | Fecha de firma |
| `cantidadSalida` | Salida |
| `cantidadLlegada` | Llegada |
| `cantidadVendida` | Vendido |
| `huecos` | Huecos |
| `rotos` | Rotos |
| `fueraEspecificacion` | Fuera de especificación, texto `motivo (cantidad)` |
| `cantidadFueraEspecificacion` | Cantidad fuera de especificación |
| `estadoVentaRuta` | Estado del trayecto |
| `mandadoALiquidacion` | Mandado a liquidación |
| `comentario` | Comentario del trayecto |

`idBitacora`, `idTrayecto` e `idVenta` sirven para abrir el detalle. No van como columnas.

Si no hay resultados, la lista llega vacía.

## Ejemplo de llamada

```http
POST /api/v1/reportes/bitacoras-ventas HTTP/1.1
Host: back.purificadoratancol.com
Authorization: Bearer <token>
Content-Type: application/json

{
  "fechaInicio": "2026-09-01T00:00:00",
  "fechaFin": "2026-09-30T23:59:59",
  "rotoPorMaquina": true
}
```
