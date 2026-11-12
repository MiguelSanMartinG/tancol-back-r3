package com.main.tancol.almacen.entities;

import com.main.tancol.almacen.entities.partidas.PartidaTransferenciaEntity;
import com.main.tancol.common.entities.base.BaseEntity;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "transferencias_material")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferenciasEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID idTransferencia;
    @Column
    private UUID idEmpleado;

    @OneToMany(mappedBy = "padrePartida", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartidaTransferenciaEntity> partidas = new ArrayList<>();

    @Column(name = "motivo")
    private String motivo;

    @ManyToOne
    @JoinColumn(name = "empleados_id")
    private EmpleadosEntity empleado;

    @Column(name = "fecha_transferencia")
    private LocalDateTime fechaTransferencia;

    @Column(name = "comentarios", length = 510)
    private String comentarios;

// Agrega m??s campos aqu??
}
