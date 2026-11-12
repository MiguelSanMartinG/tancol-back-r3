package com.main.tancol.proveedor.entity;

import com.main.tancol.common.entities.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tipos_proveedores")
public class TipoProveedorEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(name = "nombre")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String nombre;

    @Column(name = "alias")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String alias;

    @ManyToMany(mappedBy = "tiposProveedor")
    private List<ProveedorEntity> proveedorEntities = new ArrayList<>();



// Agrega m??s campos aqu??
}
