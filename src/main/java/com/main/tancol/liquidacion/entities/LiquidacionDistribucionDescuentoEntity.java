package com.main.tancol.liquidacion.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;


@Entity
@Table(name = "liquidacion_distribucion_descuento")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LiquidacionDistribucionDescuentoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "liquidacion_partida_entity_id_partida")
    private LiquidacionPartidaEntity liquidacionPartidaEntity;

    @Column(precision = 19, scale = 2)
    private BigDecimal cuentaA;

    @Column(precision = 19, scale = 2)
    private BigDecimal cuentaB;

    @Column(precision = 19, scale = 2)
    private BigDecimal cuentaC;


// Agrega m??s campos aqu??
}
