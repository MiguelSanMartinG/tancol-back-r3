package com.main.tancol.logistica.entities.rutas;

import com.main.tancol.common.entities.base.BaseEntity;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.logistica.enums.TipoRuta;
import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Slf4j
@Entity
@Table(name = "rutas")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RutasEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(name = "numero")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String numero;

    @Column(name = "nombre")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "chofer_base")
    private EmpleadosEntity chofer;

    @ManyToOne
    @JoinColumn(name = "producto_base")
    private ProductoComercialEntity productBase;

    @ManyToOne
    @JoinColumn(name = "vehiculos_id")
    private VehiculosEntity vehiculo;

    @Column(name = "letra_predeterminada")
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String letraPredeterminada;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_ruta", nullable = false, length = 50)
    private TipoRuta tipoRuta = TipoRuta.RP;

    @ManyToMany
    @JoinTable(name = "rutas_empleados",
            joinColumns = @JoinColumn(name = "rutas_id"),
            inverseJoinColumns = @JoinColumn(name = "empleados_id"))
    private Set<EmpleadosEntity> empleadosActuales = new LinkedHashSet<>();



    public void agregarEmpleado(EmpleadosEntity empleado){
        empleado.getRutasAsignadas().add(this);
        empleadosActuales.add(empleado);
        log.info("Empleado agregado: " + empleado.getNombre());
    }

    public void  eliminarEmpleado(EmpleadosEntity empleado){
        empleado.getRutasAsignadas().remove(this);
        empleadosActuales.remove(empleado);
        log.info("Empleado eliminado: " + empleado.getNombre());
    }

    public void  actualizarLista(List<EmpleadosEntity> NuevosEmpleados){
        Set<EmpleadosEntity> empleadosActuales = new LinkedHashSet<>(this.empleadosActuales);
        for (EmpleadosEntity empleadoActual : empleadosActuales) {
        // Si entre la nueva lista no hay un empleado actual borralo.
            if(!NuevosEmpleados.contains(empleadoActual)){
                eliminarEmpleado(empleadoActual);
            }
        }
        // Si entre la lista actual hay un nuevo empleado asignalo.
        for (EmpleadosEntity nuevoEmpleado : NuevosEmpleados){
            if(!empleadosActuales.contains(nuevoEmpleado)){
                agregarEmpleado(nuevoEmpleado);
            }
        }
    }

}
