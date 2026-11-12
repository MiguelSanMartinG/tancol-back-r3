package com.main.tancol.empleados.entities;

import com.main.tancol.common.entities.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Slf4j
@Table(name = "departamentos")
public class DepartamentoEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    @NotBlank(message = "Debe tener un nombre")
    @Column(name = "nombre")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String nombre;

    @NotBlank
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "alias")
    private String alias;

    @Column(name = "descripcion")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String descripcion;


    @OneToMany(mappedBy = "departamento", orphanRemoval = true)
    private List<EmpleadosEntity> empleadosEntities = new ArrayList<>();

}
