package com.main.tancol.proveedor.entity;

import com.main.tancol.auth.entities.base.BaseEntity;
import com.main.tancol.producto.entities.PrecioEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@SuperBuilder
@Slf4j
@Table(name = "proveedores")
public class ProveedorEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    @NotNull(message = "Debe tener un nombre")
    @Column(name = "nombre")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String nombre;

    @OneToMany(mappedBy = "proveedorEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PrecioEntity> productosProveedoreEntities = new LinkedHashSet<>();

    @NotBlank
    @Column(name = "razon_social")
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String razonSocial;
}
