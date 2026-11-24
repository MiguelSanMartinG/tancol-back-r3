package com.main.tancol.preferencias;

import com.main.tancol.common.entities.base.BaseEntity;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

/** Preferencia de un usuario: clave y valor JSON (Buzón 7). */
@Entity
@Table(name = "usuario_preferencias")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PreferenciaUsuarioEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private EmpleadosEntity empleado;

    @Column(name = "clave", nullable = false, length = 150)
    private String clave;

    /** Valor serializado como JSON. */
    @Column(name = "valor", columnDefinition = "TEXT")
    private String valor;
}
