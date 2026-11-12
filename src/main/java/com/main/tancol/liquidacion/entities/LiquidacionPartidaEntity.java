package com.main.tancol.liquidacion.entities;

import com.main.tancol.cliente.entities.ClienteEntity;
import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.liquidacion.enums.EstadosLiquidacion;
import com.main.tancol.liquidacion.enums.FormaPagoLiquidacion;
import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;


@Slf4j
@Entity
@Table(name = "liquidacion_partidas")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LiquidacionPartidaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID idPartida;

    @Column(name = "folio")
    private Long folio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conceptos_ventas_id")
    private ConceptosVentasEntity conceptosVentas;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private ClienteEntity cliente;

    @ManyToOne
    @JoinColumn(name = "producto_comercial_id")
    private ProductoComercialEntity productoComercial;

    @Column(name = "cantidad", precision = 19, scale = 2)
    private BigDecimal cantidad;

    @Column(name = "precio", precision = 19, scale = 2)
    private BigDecimal precio;

    @Column(name = "descuento", precision = 19, scale = 2)
    private BigDecimal descuento;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "distribucion_descuento_cuentas")
    private LiquidacionDistribucionDescuentoEntity distribucionDescuento;

    @Column(name = "importe", precision = 19, scale = 2)
    private BigDecimal importe;

    @Column(name = "importe_descontado", precision = 19, scale = 2)
    private BigDecimal importeDescontado;

    @Column(name = "factura")
    private boolean factura;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pago")
    private FormaPagoLiquidacion formaPago;

    @ManyToOne
    @JoinColumn(name = "liquidacion_origen_id_liquidacion")
    private LiquidacionEntity liquidacionOrigen;

    @Column(name = "cancelada", nullable = false)
    @Builder.Default
    private boolean cancelada = false;

    @Column(name = "motivo_cancelacion", length = 500)
    private String motivoCancelacion;

    @Column(name = "usuario_cancelacion")
    private String usuarioCancelacion;

    @Column(name = "fecha_cancelacion")
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private LocalDateTime fechaCancelacion;

    public void calcularImporte() {
        if (cancelada) {
            importe = BigDecimal.ZERO;
            return;
        }
        if (precio == null) {
            precio = new BigDecimal(0);
        }
        if (descuento == null) {
            descuento = new BigDecimal(0);
        }
        if (liquidacionOrigen.getEstadosLiquidacion().equals(EstadosLiquidacion.FINALIZADA)) {
            if (precio.compareTo(descuento) < 0) {
                throw new IDNotFound("El precio es menor que el descuento");
            }
            if (cantidad == null) {
                log.info("Ha habido un error no se cuenta con cantidades");
            } else {

                importe = cantidad.multiply(precio.subtract(descuento));
            }
        }


//        calcularImporteDescontado();
    }

//    public void calcularImporteDescontado() {
//        if(ObjectUtils.isNotEmpty(productoComercial) && !Objects.equals(importe, BigDecimal.ZERO)) {
//            var costoGeneral = productoComercial.getPrecio().multiply(cantidad).setScale(2, RoundingMode.DOWN);
//            importeDescontado = costoGeneral.subtract(importe).setScale(2, RoundingMode.DOWN);
//        }else {
//            importeDescontado = BigDecimal.ZERO;
//        }
//
//
//    }

    @PrePersist
    public void prePersist() {
        calcularImporte();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        LiquidacionPartidaEntity that = (LiquidacionPartidaEntity) o;
        return getIdPartida() != null && Objects.equals(getIdPartida(), that.getIdPartida());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
