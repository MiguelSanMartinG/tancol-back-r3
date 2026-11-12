package com.main.tancol.tabuladorrotos.entities;

import com.main.tancol.almacen.entities.UnidadesMedidaEntity;
import com.main.tancol.common.entities.base.BaseEntity;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
import com.main.tancol.tabuladorrotos.enums.PeriodoTabuladorRoto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "tabulador_botellones_rotos")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TabuladorBotellonRotoEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private EmpleadosEntity empleado;

    @Enumerated(EnumType.STRING)
    @Column(name = "periodo", nullable = false, length = 20)
    @Builder.Default
    private PeriodoTabuladorRoto periodo = PeriodoTabuladorRoto.SEMANAL;

    @Column(name = "tolerancia", precision = 19, scale = 2, nullable = false)
    private BigDecimal tolerancia;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_comercial_id", nullable = false)
    private ProductoComercialEntity producto;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_medida_id", nullable = false)
    private UnidadesMedidaEntity unidadMedida;
}
