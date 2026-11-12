package com.main.tancol.almacen.entities;

import com.main.tancol.common.entities.base.BaseEntity;
import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;


@Entity
@Table(
        name = "inventarios",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_inventarios_almacen_producto",
                columnNames = {"almacen_id", "producto_basico_entity_id"}
        )
)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventarioEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(name = "cantidad")
    private BigDecimal cantidad;

    @ManyToOne
    @JoinColumn(name = "almacen_id")
    private AlmacenEntity almacen;

    @ManyToOne
    @JoinColumn(name = "producto_basico_entity_id")
    private ProductoBasicoEntity productoBasicoEntity;

    @ManyToOne
    @JoinColumn(name = "unidades_medida_entity_id")
    private UnidadesMedidaEntity unidadDeMedida;


}
