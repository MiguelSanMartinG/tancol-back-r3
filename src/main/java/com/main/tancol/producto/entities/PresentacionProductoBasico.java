package com.main.tancol.producto.entities;

import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import com.main.tancol.proveedor.entity.ProveedorEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@SuperBuilder
@Slf4j
@Table(name = "presentaciones_producto_basico",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_producto_proveedor",
                        columnNames = {"producto_id", "proveedor_id"}
                )
        })
@ToString
public class PresentacionProductoBasico {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    @JdbcTypeCode(SqlTypes.UUID)
    private ProductoBasicoEntity productoBasicoEntity;

    @ManyToOne
    @JoinColumn(name = "proveedor_id")
    @JdbcTypeCode(SqlTypes.UUID)
    private ProveedorEntity proveedorEntity;

    @Column(name = "precio",precision = 21, scale = 8)
    private BigDecimal precio;

    @Column(name = "createdAt", updatable = false)
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private LocalDateTime createdAt;

//    @ToString.Exclude
//    @OneToMany(mappedBy = "presentacionProducto", cascade = CascadeType.ALL)
//    private List<InventarioEntity> inventarioEntities = new ArrayList<>();

}
