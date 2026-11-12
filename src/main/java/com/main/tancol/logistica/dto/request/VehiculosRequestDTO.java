package com.main.tancol.logistica.dto.request;

import com.main.tancol.almacen.entities.UnidadesMedidaEntity;
import com.main.tancol.logistica.enums.EstatusVehiculo;
import jakarta.persistence.Column;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class VehiculosRequestDTO {
    // Agrega campos aqu??
    String nombre;
    String placas;
    BigDecimal capacidadMaxima;
    UUID unidadMedida;
    String numeroPoliza;
    LocalDateTime vigenciaPoliza;
    String documentoPoliza;
    EstatusVehiculo statusVehiculo;
    String descripcion;
    boolean enabled;

}
