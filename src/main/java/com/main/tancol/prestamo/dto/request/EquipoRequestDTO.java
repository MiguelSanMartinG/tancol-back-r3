package com.main.tancol.prestamo.dto.request;

import lombok.Value;

import java.util.UUID;

@Value
public class EquipoRequestDTO {
    String nombre;
    String sku;
    String idImagen;
    String descripcion;
    String marca;
    String modelo;
    UUID idTipoEquipo;
}
