package com.main.tancol.producto.entities;

import com.main.tancol.auth.entities.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@NoArgsConstructor
@ToString(callSuper = true)
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

    @OneToMany(mappedBy = "categoriaProductoEntity")
    private List<ProductoEntity> productoEntities = new ArrayList<>();

}
