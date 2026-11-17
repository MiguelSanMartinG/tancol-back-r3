package com.main.tancol.cuadreinventario.dto;

import com.main.tancol.cuadreinventario.enums.EstadoCuadre;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/** Request y response del cuadre de inventario (contrato con el front). */
public final class CuadreInventarioDTOs {

    private CuadreInventarioDTOs() {
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CrearRequest {
        @NotNull
        private UUID idAlmacen;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CapturaPartida {
        @NotNull
        private UUID idPartida;
        @DecimalMin(value = "0.0", inclusive = true)
        private BigDecimal cantidadFisica;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CapturaRequest {
        @Valid
        private List<CapturaPartida> partidas = List.of();
        private String observaciones;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CerrarRequest {
        @NotBlank
        private String idImagenFirma;
        @NotNull
        private Boolean ajustarExistencia;
        private String observaciones;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class AlmacenRef {
        private UUID id;
        private String nombre;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class PartidaResponse {
        private UUID idPartida;
        private UUID idProducto;
        private String producto;
        private String sku;
        private String unidad;
        private BigDecimal cantidadSistema;
        private BigDecimal cantidadFisica;
        private BigDecimal diferencia;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Response {
        private UUID id;
        private String folio;
        private AlmacenRef almacen;
        private EstadoCuadre estado;
        private String creadoPor;
        private LocalDateTime fechaCreacion;
        private String cerradoPor;
        private LocalDateTime fechaCierre;
        private boolean ajusteAplicado;
        private int totalPartidas;
        private int partidasCapturadas;
        private int partidasConDiferencia;
        /** Solo en el detalle. */
        private String observaciones;
        private String idImagenFirma;
        private LocalDateTime fechaFirma;
        private List<PartidaResponse> partidas;
    }
}
