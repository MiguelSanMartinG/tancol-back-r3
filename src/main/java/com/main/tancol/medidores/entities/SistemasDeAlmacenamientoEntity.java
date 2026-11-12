package com.main.tancol.medidores.entities;

import com.main.tancol.common.entities.base.BaseEntity;
import com.main.tancol.empleados.entities.DepartamentoEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;


@Entity
@Table(name = "sistemas_almacenamiento")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SistemasDeAlmacenamientoEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    private String nombre;

    private String alias;

    @ManyToOne
    @JoinColumn(name = "departamento_id")
    private DepartamentoEntity departamento;

    @Column(name = "capacidad", precision = 19, scale = 2)
    private BigDecimal capacidad;

// Agrega m??s campos aqu??
}
