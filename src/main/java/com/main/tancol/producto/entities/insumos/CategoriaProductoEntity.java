package com.main.tancol.producto.entities.insumos;

import com.main.tancol.common.entities.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Slf4j
@AllArgsConstructor
@Table(name = "categoria_producto")
public class CategoriaProductoEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private UUID id;

    @Column(name = "nombre")
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_padre_id")
    private CategoriaProductoEntity categoriaPadre;

    @Builder.Default
    @OneToMany(mappedBy = "categoriaPadre")
    private List<CategoriaProductoEntity> subcategorias = new ArrayList<>();

    @OneToMany(mappedBy = "categoriaProductoEntity", cascade = CascadeType.ALL)
    private List<ProductoBasicoEntity> productoEntities = new ArrayList<>();

}
