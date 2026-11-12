package com.main.tancol.liquidacion.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "reparticiones_bitacora")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReparticionEntreCuentasEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @Column(name = "cuenta_a", precision = 19, scale = 2)
    private BigDecimal cuentaA;

    @Column(name = "cuenta_b", precision = 19, scale = 2)
    private BigDecimal cuentaB;

    @Column(name = "cuenta_c", precision = 19, scale = 2)
    private BigDecimal cuentaC;

    @Column(name = "usuario_que_edito")
    private String usuarioQueEdito;

    @Column(name = "createdAt", updatable = false)
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private LocalDateTime updatedAt;

    private boolean deleted;


    @PrePersist
    public void prePersist() {
        updatedAt = LocalDateTime.now();
        createdAt = LocalDateTime.now();
//        origenBitacora = false;
//        totalAbsoluto = totalAbsoluto();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
//        totalAbsoluto = totalAbsoluto();
    }

// Agrega m??s campos aqu??
}
