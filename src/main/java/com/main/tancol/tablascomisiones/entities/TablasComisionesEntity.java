package com.main.tancol.tablascomisiones.entities;

import com.main.tancol.common.entities.base.BaseEntity;
import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "tablas_comisiones")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TablasComisionesEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(name = "venta_minima", nullable = false)
    private Integer ventaMinima;

    @Column(name = "cantidad_desde", nullable = false)
    private Integer cantidadDesde;

    @Column(name = "cantidad_hasta", nullable = false)
    private Integer cantidadHasta;

    @Column(name = "comision", precision = 19, scale = 2, nullable = false)
    private BigDecimal comision;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_comercial_id", nullable = false)
    private ProductoComercialEntity producto;
}
