package com.main.tancol.producto.entities.comerciales;

import com.main.tancol.common.entities.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "categoria_producto_comercial")
public class CategoriaProductoComercialEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    @Column(name = "nombre")
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String nombre;

    @OneToMany(mappedBy = "categoriaProductoComercialEntity", cascade = CascadeType.ALL)
    private List<ProductoComercialEntity> productosComerciales = new ArrayList<>();
}
