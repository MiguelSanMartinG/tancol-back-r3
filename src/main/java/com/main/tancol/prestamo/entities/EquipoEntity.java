package com.main.tancol.prestamo.entities;

import com.main.tancol.common.entities.base.BaseEntity;
import com.main.tancol.prestamo.enums.EstadoEquipo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "equipos")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EquipoEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "sku")
    private String sku;

    @Column(name = "id_imagen")
    private String idImagen;

    @Column(name = "descripcion", length = 510)
    private String descripcion;

    @Column(name = "marca")
    private String marca;

    @Column(name = "modelo")
    private String modelo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 50)
    private EstadoEquipo estado;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_equipo_id", nullable = false)
    private TipoEquipoEntity tipoEquipo;

    @OneToMany(mappedBy = "equipo")
    @Builder.Default
    private List<EquipoAsignacionEntity> asignaciones = new ArrayList<>();
}
