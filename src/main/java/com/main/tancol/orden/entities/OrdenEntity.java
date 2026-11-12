package com.main.tancol.orden.entities;

import com.main.tancol.almacen.entities.AlmacenEntity;
import com.main.tancol.common.entities.base.BaseEntity;
import com.main.tancol.orden.enums.FacturacionOrdenEstatus;
import com.main.tancol.orden.enums.Prioridad;
import com.main.tancol.orden.enums.RecepcionEstatus;
import com.main.tancol.orden.enums.SolicitudEstatus;
import com.main.tancol.proveedor.entity.ProveedorEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
@Table(name = "ordenes")
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
    private ProveedorEntity proveedor;

    @Column(name = "fecha_solicitud")
    private LocalDateTime getFechaCreacionSolicitud;

    @Column(name = "fecha_facturacion")
    private LocalDateTime fechaFacturacion;

    @Column(name = "fecha_recibo")
    private LocalDateTime fechaRecibo;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name = "prioridad")
    private Prioridad prioridad;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name="solicitante_estado")
    private SolicitudEstatus solicitudEstatus;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name="facturacion_estado")
    private FacturacionOrdenEstatus facturacionEstado;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    @Column(name="recepcion_estado")
    private RecepcionEstatus recepcionEstatus;

    @Column(name = "folio_externo")
    private String folioExterno;

    @Column(name = "comentario_orden")
    private String comentarioFactura;

    @Column(name = "comentario_recibo")
    private String comentarioRecibo;

    @Column(name = "total_orden")
    private BigDecimal totalOrden;

    @OneToMany(mappedBy = "solicitudCompraEntity",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<PartidaOrdenEntity> partidas = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "almacen_id")
    private AlmacenEntity almacen;

    @Column(name = "usuario_provisional")
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String usuario;

// Agrega m??s campos aqu??
}
