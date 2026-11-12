package com.main.tancol.solicitudes.entities;

import com.main.tancol.common.entities.base.BaseNoEnabledEntity;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.orden.enums.Prioridad;
import com.main.tancol.orden.enums.SolicitudEstatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "solicitudes_compra")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudEntity extends BaseNoEnabledEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "prioridad")
    private Prioridad prioridad;

    @ManyToOne
    @JoinColumn(name = "empleado_solicitante_id")
    private EmpleadosEntity empleadoSolicitante;

    @Column(name = "uso")
    private String uso;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name="estado_solicitud")
    private SolicitudEstatus solicitudEstatus;

    @OneToMany(mappedBy = "padre", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SolicitudPartidaEntity> listaPartidas = new LinkedHashSet<>();

    @Column(length = 510)
    private String comentario;

}
