package com.main.tancol.tablasamonestaciones.entities;

import com.main.tancol.common.entities.base.BaseEntity;
import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "tablas_amonestaciones")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TablasAmonestacionesEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(name = "cantidad_desde", nullable = false)
    private Integer cantidadDesde;

    @Column(name = "cantidad_hasta", nullable = false)
    private Integer cantidadHasta;

    @Column(name = "descuento", precision = 19, scale = 2, nullable = false)
    private BigDecimal descuento;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_comercial_id", nullable = false)
    private ProductoComercialEntity producto;
}
