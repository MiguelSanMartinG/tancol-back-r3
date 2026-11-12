package com.main.tancol.producto.entities;

import com.main.tancol.auth.entities.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@ToString(callSuper = true)
@Getter
@Setter
@Entity
@SuperBuilder
@Slf4j
@AllArgsConstructor
@Table(name = "productos")
public class ProductoEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    @NotBlank(message = "Debe tener un nombre")
    @Column(name = "nombre", unique = true, nullable = false)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @NotBlank
    @Column(name="SKU")
    private String sku;

    @Column
    private String alias;


    @OneToMany(mappedBy = "productoEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<PrecioEntity> productosProveedoreEntities;


    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "categoria_producto_id")
    private CategoriaProductoEntity categoriaProductoEntity;

}
