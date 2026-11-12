package com.main.tancol.logistica.entities.bitacora;

import com.main.tancol.logistica.entities.bitacora.comisiones.ProductosFueraEspecificacionEntity;
import com.main.tancol.logistica.entities.bitacora.comisiones.VentasEmpleadoEntity;
import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Entity
@Table(name = "bitacora_rutas_ventas_trayectos_producto")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VentaTrayectoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID idVenta;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private ProductoComercialEntity producto;

    @Column(precision = 19, scale = 2)
    private BigDecimal cantidad;

    @Column(name = "cantidad_salida", precision = 19, scale = 2)
    private BigDecimal cantidadSalida;

    @Column(name = "cantidad_llegada", precision = 19, scale = 2)
    private BigDecimal cantidadLlegada;

    @OneToMany(mappedBy = "trayectoOrigen", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<VentasEmpleadoEntity> comisionesEmpleados = new LinkedHashSet<>();

    public void removerComision(VentasEmpleadoEntity partida) {
        partida.setTrayectoOrigen(null);
        comisionesEmpleados.remove(partida);
    }
    public void agregarComision(VentasEmpleadoEntity partida) {
        partida.setTrayectoOrigen(this);
        comisionesEmpleados.add(partida);
    }

    @OneToMany(mappedBy = "ventaOrigen", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ProductosFueraEspecificacionEntity> productosFueraEspecificacion = new LinkedHashSet<>();

    public void removerProductoConEspecificacion(ProductosFueraEspecificacionEntity partida) {
        partida.setVentaOrigen(null);
        productosFueraEspecificacion.remove(partida);

    }

    public void agregarProductoConEspecificacion(ProductosFueraEspecificacionEntity partida) {
        partida.setVentaOrigen(this);
        productosFueraEspecificacion.add(partida);
    }

    @OneToMany(mappedBy = "ventaOrigen", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<MermaVentaTrayectoEntity> mermas = new LinkedHashSet<>();

    public void removerMerma(MermaVentaTrayectoEntity merma) {
        merma.setVentaOrigen(null);
        mermas.remove(merma);
    }

    public void agregarMerma(MermaVentaTrayectoEntity merma) {
        if (mermas == null) {
            mermas = new LinkedHashSet<>();
        }
        merma.setVentaOrigen(this);
        mermas.add(merma);
    }

    @ManyToOne
    @JoinColumn(name = "trayecto_origen_id_trayecto")
    private TrayectoEntity trayectoOrigen;

    @Column(name = "huecos", precision = 19, scale = 2)
    private BigDecimal huecos;

    @Column(name = "createdAt", updatable = false)
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private LocalDateTime updatedAt;

    public BigDecimal totalMermas() {
        if (mermas == null || mermas.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return mermas.stream()
                .map(merma -> merma.getCantidad() != null ? merma.getCantidad() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void calcularVendidos() {
        this.cantidad = BigDecimal.ZERO;

        if (cantidadSalida != null && cantidadLlegada != null) {
            BigDecimal huecosVal = huecos != null ? huecos : BigDecimal.ZERO;
            this.cantidad = cantidadSalida.subtract(cantidadLlegada).subtract(huecosVal).add(totalMermas());
        }
    }

    @PrePersist
    public void prePersist() {
        updatedAt = LocalDateTime.now();
        createdAt = LocalDateTime.now();
        calcularVendidos();
    }

    @PreUpdate
    public void preUpdate() {
        calcularVendidos();
        updatedAt = LocalDateTime.now();
    }

}
