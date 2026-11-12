package com.main.tancol.logistica.entities.rutas;

import com.main.tancol.almacen.entities.UnidadesMedidaEntity;
import com.main.tancol.common.entities.base.BaseEntity;
import com.main.tancol.logistica.enums.EstatusVehiculo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "vehiculos")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehiculosEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String nombre;

    @Column(name = "capacidad_maxima")
    @JdbcTypeCode(SqlTypes.INTEGER)
    private BigDecimal capacidadMaxima;

    @ManyToOne
    @JoinColumn(name = "unidad_medida_id")
    private UnidadesMedidaEntity unidadesMedidaEntity;

    @Column(name = "placas")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String placas;

    @Enumerated(EnumType.STRING)
    @Column(name = "estatus_vehiculo")
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private EstatusVehiculo estatusVehiculo;

    @Column(name = "numero_polisa")
    private String numeroPoliza;

    @Column(name = "vigencia_poliza")
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private LocalDateTime vigenciaPoliza;

    @Column(name = "documento_poliza")
    private String documentoPoliza;

    @Column(name = "descripcion", length = 510)
    private String descripcion;

// Agrega m??s campos aqu??
}
