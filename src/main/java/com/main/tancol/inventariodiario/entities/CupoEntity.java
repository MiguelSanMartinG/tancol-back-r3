package com.main.tancol.inventariodiario.entities;

import com.main.tancol.almacen.entities.AlmacenEntity;
import com.main.tancol.common.entities.base.BaseEntity;
import com.main.tancol.empleados.entities.DepartamentoEntity;
import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;

/** Tarjeta: cupo base diario de un insumo para un área. */
@Entity
@Table(name = "inv_diario_cupos")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CupoEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id", nullable = false)
    private DepartamentoEntity area;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_id", nullable = false)
    private AlmacenEntity almacen;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoBasicoEntity insumo;

    @Column(name = "por_defecto", nullable = false)
    private boolean porDefecto;

    @Column(name = "base", nullable = false, precision = 19, scale = 2)
    private BigDecimal base;

    @Column(name = "minimo", nullable = false, precision = 19, scale = 2)
    private BigDecimal minimo;
}
