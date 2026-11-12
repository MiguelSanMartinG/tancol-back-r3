package com.main.tancol.producto.entities;

import com.main.tancol.auth.entities.base.BaseEntity;
import com.main.tancol.proveedor.entity.ProveedorEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@SuperBuilder
@Slf4j
@Table(name = "precios")
public class PrecioEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    @JdbcTypeCode(SqlTypes.UUID)
    private ProductoEntity productoEntity;

    @ManyToOne
    @JoinColumn(name = "proveedor_id")
    @JdbcTypeCode(SqlTypes.UUID)
    private ProveedorEntity proveedorEntity;

    @Column(name = "precio",precision = 21, scale = 8)
    private BigDecimal precio;



}
