package com.main.tancol.permisos.entities;

import com.main.tancol.common.entities.base.BaseEntity;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "empleado_permiso_avanzado")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoPermisoAvanzadoEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private EmpleadosEntity empleado;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "permiso_avanzado_id", nullable = false)
    private PermisoAvanzadoEntity permiso;

    @Column(name = "asignado_por")
    private String asignadoPor;
}
