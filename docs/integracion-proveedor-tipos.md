# Integración: varios tipos de proveedor

Un proveedor puede tener más de un tipo. El alta y la edición reemplazan la lista completa.

Requiere JWT.

```
Authorization: Bearer <token>
Content-Type: application/json
```

Base local: `/api/v1`. Producción: `https://back.purificadoratancol.com/api/v1`.

## Catálogo de tipos

Para llenar el selector:

```
GET /api/v1/tipo-proveedor
```

Cada tipo trae `id`, `nombre` y `alias`. Esos `id` son los que se envían en `idsTipoProveedor`.

## Alta y edición

```
POST /api/v1/proveedor
PUT /api/v1/proveedor/{id}
```

`nombreComercial` es obligatorio. Hace falta al menos un tipo.

| Campo | Tipo | Uso |
| --- | --- | --- |
| `nombreComercial` | `string` | Obligatorio. |
| `idsTipoProveedor` | `UUID[]` | Tipos del proveedor. Si este campo viene, aunque sea `[]`, es la lista que se guarda. |
| `idTipoProveedor` | `UUID` | Un solo tipo. Solo se usa cuando `idsTipoProveedor` no viene en el JSON. |
| `condicionPago` | `string` | `CONTADO`, `CONTADO_FACTURA`, `CREDITO`, `VALE_EFECTIVO`, `VALE_TRANSFERENCIA`, `DONACION`, `OTRO`. |
| `correo` | `string` | Opcional. |
| `telefono` | `string` | Opcional. |
| `informacionFiscal` | objeto | Opcional. Misma forma de siempre. |

Si `idsTipoProveedor` viene en el cuerpo, sustituye todos los tipos anteriores. No se suma al tipo viejo. Para quitar un tipo, mándalo fuera de la lista. Los ids repetidos se guardan una sola vez.

Ejemplo de alta o edición:

```json
{
  "nombreComercial": "Empaques del Norte",
  "idsTipoProveedor": [
    "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "7c9e6679-7425-40de-944b-e07fc1f90ae7"
  ],
  "condicionPago": "CREDITO",
  "correo": "compras@empaques.com",
  "telefono": "5555555555"
}
```

Un solo tipo, forma anterior, sigue funcionando si no se manda `idsTipoProveedor`:

```json
{
  "nombreComercial": "Empaques del Norte",
  "idTipoProveedor": "3fa85f64-5717-4562-b3fc-2c963f66afa6"
}
```

## Response `201` en alta y `200` en edición, consulta y listado

```json
{
  "idProveedor": "1b4e28ba-2fa1-11d2-883f-0016d3cca427",
  "nombreComercial": "Empaques del Norte",
  "nombreTipoProveedor": "Insumos",
  "idTipoProveedor": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "tiposProveedor": [
    {
      "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
      "nombre": "Insumos",
      "alias": "INS"
    },
    {
      "id": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
      "nombre": "Servicios",
      "alias": "SRV"
    }
  ],
  "condicionPago": "CREDITO",
  "correo": "compras@empaques.com",
  "telefono": "5555555555",
  "infoFiscal": null,
  "enabled": true,
  "createdAt": "2026-10-23T08:00:00",
  "updatedAt": "2026-10-23T08:10:00"
}
```

`tiposProveedor` es la lista para la pantalla. Viene ordenada por nombre.

`idTipoProveedor` y `nombreTipoProveedor` siguen en la respuesta. Son el primer tipo de esa lista, el de nombre más bajo. No alcanzan para pintar todos los tipos.

Al abrir la edición, marca en el selector los `id` de `tiposProveedor`. Al guardar, vuelve a mandar el arreglo completo en `idsTipoProveedor`.

## Listado

```
GET /api/v1/proveedor
GET /api/v1/proveedor?idTipoProveedor={uuid}
```

`idTipoProveedor` deja en el listado a los proveedores que tengan ese tipo, aunque también tengan otros.

Cada fila trae el mismo `tiposProveedor` que el detalle.

## Errores `409`

| Mensaje | Cuándo |
| --- | --- |
| `Debe indicar al menos un tipo de proveedor` | No viene `idsTipoProveedor` ni `idTipoProveedor`, o `idsTipoProveedor` llega vacío. |
| `No existe la categoría de proveedor` | Algún id no existe en el catálogo. |
| `Debe tener un nombre` | Falta `nombreComercial`. |
