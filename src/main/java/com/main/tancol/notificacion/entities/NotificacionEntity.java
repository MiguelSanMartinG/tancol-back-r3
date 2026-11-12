package com.main.tancol.notificacion.entities;

import com.main.tancol.common.entities.base.BaseEntity;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.notificacion.enums.TipoNotificacion;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notificaciones")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "mensaje", nullable = false, length = 2000)
    private String mensaje;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoNotificacion tipo;

    @Column(name = "leida", nullable = false)
    @Builder.Default
    private boolean leida = false;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private EmpleadosEntity empleado;

    @Column(name = "asignacion_id")
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID asignacionId;

    @Column(name = "cliente_id")
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID clienteId;

    @Column(name = "meta_venta_id")
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID metaVentaId;

    @Column(name = "producto_id")
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID productoId;

    @Column(name = "empleado_evaluado_id")
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID empleadoEvaluadoId;

    @Column(name = "periodo_inicio")
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private LocalDateTime periodoInicio;

    @Column(name = "periodo_fin")
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private LocalDateTime periodoFin;
}
