package com.main.tancol.almacen.entities;

import com.main.tancol.common.entities.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@ToString(callSuper = true)
@Getter
@Setter
@Entity
@Builder
@Slf4j
@AllArgsConstructor
@Table(name = "almacenes")
public class AlmacenEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    @Column(name = "nombre")
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String nombre;

    /** Responsable del almacén: recibe las notificaciones del inventario diario. */
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "responsable_id")
    private com.main.tancol.empleados.entities.EmpleadosEntity responsable;

    @ToString.Exclude
    @OneToMany(mappedBy = "almacen", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InventarioEntity> inventario = new ArrayList<>();

}