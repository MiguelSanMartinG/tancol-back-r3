package com.main.tancol.medidores.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "bitacoras_medicion")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BitacoraMedicionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(name = "medida_actual", precision = 19, scale = 2)
    private BigDecimal medidaActual;

    @Column(name = "medida_inicial", precision = 19, scale = 2)
    private BigDecimal medidaInicial;

    @ManyToOne
    @JoinColumn(name = "medidor_id_medidor")
    private MedidoresEntity medidor;

    @Column(name = "comentario")
    private String comentario;

    @Column(name = "consumo", precision = 19, scale = 2)
    private BigDecimal consumo;

    @Column(name = "liquidado", precision = 19, scale = 2)
    private BigDecimal liquidado;

    @Column(name = "quedado_en_pipa", precision = 19, scale = 2)
    private BigDecimal quedadoEnPipa;

    @Column(name = "litros_minuto", precision = 19, scale = 2)
    private BigDecimal litrosMinuto;

    @Column(name = "diferencia", precision = 19, scale = 2)
    private BigDecimal diferencia;

    @Column(name = "usuario")
    private String nombreUsuario;

    @Column(name = "createdAt", updatable = false)
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        updatedAt = LocalDateTime.now();
        createdAt = LocalDateTime.now();
        calcular();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
        calcular();
    }

    private void calcular() {
        if (medidaInicial == null || medidaActual == null) {
            throw new IllegalStateException("medidaInicial or medidaActual is null");
        }
        if (consumo == null) {
            consumo = medidaActual.subtract(medidaInicial);
        }
        BigDecimal liquidadoValor = liquidado != null ? liquidado : BigDecimal.ZERO;
        BigDecimal quedadoEnPipaValor = quedadoEnPipa != null ? quedadoEnPipa : BigDecimal.ZERO;
        diferencia = consumo.subtract(liquidadoValor).subtract(quedadoEnPipaValor);
    }
}
