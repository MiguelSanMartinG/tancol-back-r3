package com.main.tancol.inventariodiario.entities;

import com.main.tancol.common.entities.base.BaseEntity;
import com.main.tancol.empleados.entities.DepartamentoEntity;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.inventariodiario.enums.ConceptoEntrega;
import com.main.tancol.logistica.entities.rutas.RutasEntity;
import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/** Partida (entrega) del inventario diario: descuenta del cupo del día y del almacén. */
@Entity
@Table(name = "inv_diario_partidas")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartidaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(name = "folio", nullable = false, length = 20)
    private String folio;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id", nullable = false)
    private DepartamentoEntity area;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ruta_id")
    private RutasEntity ruta;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoBasicoEntity insumo;

    @Column(name = "cantidad", nullable = false, precision = 19, scale = 2)
    private BigDecimal cantidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "concepto", nullable = false, length = 20)
    private ConceptoEntrega concepto;

    @Column(name = "comentario", length = 500)
    private String comentario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roto_por_id")
    private EmpleadosEntity rotoPor;

    @Column(name = "roto_por_maquina", nullable = false)
    private boolean rotoPorMaquina;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private EmpleadosEntity usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "firmante_id")
    private EmpleadosEntity firmante;

    @Column(name = "id_imagen_firma", length = 300)
    private String idImagenFirma;

    @Column(name = "fecha_firma")
    private LocalDateTime fechaFirma;
}
