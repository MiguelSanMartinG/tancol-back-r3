package com.main.tancol.liquidacion.entities;

import com.main.tancol.logistica.entities.bitacora.TrayectoEntity;
import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "liquidacion_totalizado")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LiquidacionTotalizadoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @ManyToOne( fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_comercial_id")
    private ProductoComercialEntity productoComercial;

    @OneToMany(mappedBy = "origenTotalizado", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<LiquidacionTotalizadoEmpleadoEntity> empleadoTotalizadoLiquidacion = new LinkedList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origen_liquidacion_id_liquidacion")
    private LiquidacionEntity liquidacionOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trayecto_id")
    private TrayectoEntity trayecto;

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ")";
    }
// Agrega m??s campos aqu??
}
