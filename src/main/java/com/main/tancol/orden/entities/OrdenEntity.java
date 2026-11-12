package com.main.tancol.orden.entities;

import com.main.tancol.almacen.entities.Almacen;
import com.main.tancol.auth.entities.base.BaseEntity;
import com.main.tancol.orden.enums.FacturacionOrdenEstatus;
import com.main.tancol.orden.enums.Prioridad;
import com.main.tancol.orden.enums.RecepcionEstatus;
import com.main.tancol.orden.enums.SolicitanteEstatus;
import com.main.tancol.proveedor.entity.ProveedorEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdenEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column
    private String folio;

    @Column(name = "titulo")
    private String titulo;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "proveedor_id")
    private ProveedorEntity proveedorEntity;

    @Column(name = "fecha_solicitud")
    private LocalDateTime fechaSolicitud;

    @Column(name = "fecha_facturacion")
    private LocalDateTime fechaFacturacion;

    @Column(name = "fecha_recibo")
    private LocalDateTime fechaRecibo;

    @Enumerated(EnumType.STRING)
    @Column(name = "prioridad")
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private Prioridad prioridad;

    @Enumerated(EnumType.STRING)
    @Column(name="solicitante_estado")
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private SolicitanteEstatus solicitanteEstatus;

    @Enumerated(EnumType.STRING)
    @Column(name="post_autorizado_estado")
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private FacturacionOrdenEstatus postAutorizadoEstatus;

    @Enumerated(EnumType.STRING)
    @Column(name="recepcion_estado")
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private RecepcionEstatus recepcionEstatus;

    @Column(name = "folio_externo")
    private String folioExterno;

    @Column(name = "comentario_orden")
    private String comentarioFactura;

    @Column(name = "comentario_recibo")
    private String comentarioRecibo;

    @Column(name = "total_orden")
    private BigDecimal totalOrden;

    @OneToMany(mappedBy = "solicitudCompraEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartidaOrdenEntity> partidas = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "almacen_id")
    private Almacen almacen;

    @Column(name = "usuario_provisional")
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String usuario;

// Agrega m??s campos aqu??
}
