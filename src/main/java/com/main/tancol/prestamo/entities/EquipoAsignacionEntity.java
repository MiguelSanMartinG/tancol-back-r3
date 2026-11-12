package com.main.tancol.prestamo.entities;

import com.main.tancol.cliente.entities.ClienteEntity;
import com.main.tancol.common.entities.base.BaseEntity;
import com.main.tancol.prestamo.enums.PeriodoCoberturaEquipo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "equipo_asignaciones")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EquipoAsignacionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_id", nullable = false)
    private EquipoEntity equipo;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteEntity cliente;

    @Column(name = "fecha_asignacion")
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private LocalDateTime fechaAsignacion;

    @Column(name = "fecha_devolucion")
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private LocalDateTime fechaDevolucion;

    @Column(name = "monto_minimo", precision = 19, scale = 2)
    private BigDecimal montoMinimo;

    @Enumerated(EnumType.STRING)
    @Column(name = "periodo_cobertura", nullable = false)
    @Builder.Default
    private PeriodoCoberturaEquipo periodoCobertura = PeriodoCoberturaEquipo.SEMANAL;

    @Column(name = "activa", nullable = false)
    private boolean activa;
}
