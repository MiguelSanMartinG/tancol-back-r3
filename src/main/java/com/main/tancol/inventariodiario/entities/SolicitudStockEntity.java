package com.main.tancol.inventariodiario.entities;

import com.main.tancol.almacen.entities.AlmacenEntity;
import com.main.tancol.common.entities.base.BaseEntity;
import com.main.tancol.empleados.entities.DepartamentoEntity;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.inventariodiario.enums.EstatusSolicitud;
import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/** Solicitud de stock: pide al almacén ampliar el cupo del día de un insumo. */
@Entity
@Table(name = "inv_diario_solicitudes")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudStockEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(name = "folio", nullable = false, length = 20)
    private String folio;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id", nullable = false)
    private DepartamentoEntity area;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoBasicoEntity insumo;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_id", nullable = false)
    private AlmacenEntity almacen;

    @Column(name = "cantidad_solicitada", nullable = false, precision = 19, scale = 2)
    private BigDecimal cantidadSolicitada;

    @Column(name = "nota", length = 500)
    private String nota;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitado_por")
    private EmpleadosEntity solicitadoPor;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud;

    @Enumerated(EnumType.STRING)
    @Column(name = "estatus", nullable = false, length = 20)
    private EstatusSolicitud estatus;

    @Column(name = "cantidad_a_entregar", precision = 19, scale = 2)
    private BigDecimal cantidadAEntregar;

    @Column(name = "comentario", length = 500)
    private String comentario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "confirmado_por")
    private EmpleadosEntity confirmadoPor;

    @Column(name = "fecha_confirmacion")
    private LocalDateTime fechaConfirmacion;

    @Column(name = "cantidad_recibida", precision = 19, scale = 2)
    private BigDecimal cantidadRecibida;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recibido_por")
    private EmpleadosEntity recibidoPor;

    @Column(name = "fecha_recepcion")
    private LocalDateTime fechaRecepcion;

    @Column(name = "id_imagen_firma", length = 300)
    private String idImagenFirma;

    /** Firma de entrega del responsable del almacén. */
    @Column(name = "firma_almacen", length = 300)
    private String firmaAlmacen;

    @Column(name = "fecha_firma_almacen")
    private LocalDateTime fechaFirmaAlmacen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "firmado_almacen_por")
    private EmpleadosEntity firmadoAlmacenPor;
}
