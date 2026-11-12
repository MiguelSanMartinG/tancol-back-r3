package com.main.tancol.almacen.entities;

import com.main.tancol.almacen.entities.partidas.PartidaOrdenInternaEntity;
import com.main.tancol.almacen.enums.EstadoOrdenInterna;
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
@Table(name = "ordenes_internas_history")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdenInternaEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID idOrdenInterna;

    private String folio;

    @ManyToOne
    @JoinColumn(name = "empleado_id")
    private EmpleadosEntity empleadoEnvia;

    @ManyToOne
    @JoinColumn(name = "empleado_id_recibe")
    private EmpleadosEntity empleadoRecibe;

    @Column(name = "fecha_elaboracion")
    private LocalDateTime fechaElaboracion;

    @Column(name = "descripcion")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String comentarios;

    @OneToMany(mappedBy = "ordenInternaEntity", cascade = CascadeType.ALL)
    private List<PartidaOrdenInternaEntity> partidas = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String uso;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_ordeninterna")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private EstadoOrdenInterna estadoOrdenInterna;



}
