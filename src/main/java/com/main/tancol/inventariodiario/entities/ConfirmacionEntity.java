package com.main.tancol.inventariodiario.entities;

import com.main.tancol.common.entities.base.BaseEntity;
import com.main.tancol.empleados.entities.DepartamentoEntity;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/** Confirmación del cupo del día de un área (obligatoria antes de registrar partidas o pedir stock). */
@Entity
@Table(name = "inv_diario_confirmaciones")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmacionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id", nullable = false)
    private DepartamentoEntity area;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "confirmado_por")
    private EmpleadosEntity confirmadoPor;

    @Column(name = "confirmado_en", nullable = false)
    private LocalDateTime confirmadoEn;
}
