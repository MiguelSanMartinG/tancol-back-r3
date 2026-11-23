package com.main.tancol.empleados.entities;

import com.main.tancol.common.entities.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

/** Contacto de emergencia de un empleado (hasta 3, en el orden capturado). */
@Entity
@Table(name = "empleado_contactos_emergencia")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoContactoEmergenciaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private EmpleadosEntity empleado;

    @Column(name = "orden", nullable = false)
    private Integer orden;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "parentesco", length = 100)
    private String parentesco;

    @Column(name = "telefono", nullable = false, length = 30)
    private String telefono;

    @Column(name = "correo")
    private String correo;
}
