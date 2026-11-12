package com.main.tancol.liquidacion.entities;

import com.main.tancol.common.entities.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;


@Entity
@Table(name = "liquidacion_conceptos_venta")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConceptosVentasEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    private String concepto;

    @Column(name = "is_exclude")
    private boolean isExclude;

    @Column(name = "descuento")
    private boolean descuento;

    private String name;

    private boolean x;

    private boolean cancelado;


// Agrega m??s campos aqu??
}
