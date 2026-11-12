package com.main.tancol.prestamo.entities;

import com.main.tancol.common.entities.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tipos_equipo")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TipoEquipoEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "alias")
    private String alias;

    @OneToMany(mappedBy = "tipoEquipo", cascade = CascadeType.ALL)
    @OrderBy("createdAt DESC")
    @Builder.Default
    private List<EquipoEntity> equipos = new ArrayList<>();
}
