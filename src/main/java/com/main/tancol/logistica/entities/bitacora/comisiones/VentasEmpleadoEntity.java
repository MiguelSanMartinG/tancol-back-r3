package com.main.tancol.logistica.entities.bitacora.comisiones;

import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.logistica.entities.bitacora.VentaTrayectoEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;


@Entity
@Table(name = "comisiones_empleado_ruta")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VentasEmpleadoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID idComision;

    @ManyToOne
    @JoinColumn(name = "empleado_id")
    private EmpleadosEntity empleado;

    @Column(precision = 19, scale = 2)
    private BigDecimal comision;


    @ManyToOne
    @JoinColumn(name = "trayecto_origen_id_venta")
    private VentaTrayectoEntity trayectoOrigen;

}
