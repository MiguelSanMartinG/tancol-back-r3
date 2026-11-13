package com.main.tancol.metaventa.entities;

import com.main.tancol.common.entities.base.BaseEntity;
import com.main.tancol.logistica.entities.rutas.RutasEntity;
import com.main.tancol.metaventa.enums.PeriodoMetaVenta;
import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "meta_venta_ruta")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MetaVentaRutaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ruta_id", nullable = false)
    private RutasEntity ruta;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_comercial_id", nullable = false)
    private ProductoComercialEntity producto;

    @Column(name = "cantidad_minima", precision = 19, scale = 2, nullable = false)
    private BigDecimal cantidadMinima;

    @Enumerated(EnumType.STRING)
    @Column(name = "periodo", nullable = false)
    @Builder.Default
    private PeriodoMetaVenta periodo = PeriodoMetaVenta.SEMANAL;

    @Column(name = "activa", nullable = false)
    private boolean activa;

    @Column(name = "fecha_inicio")
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_cierre")
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private LocalDateTime fechaCierre;
}
