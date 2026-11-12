package com.main.tancol.liquidacion.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "liquidacion_totalizado_empleado")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LiquidacionTotalizadoEmpleadoEntity {
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "cantidad = " + cantidad + ", " +
                "empleado = " + empleado + ")";
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // Asegúrate de que esto funcione como esperas
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "empleado_id")
    private EmpleadosEntity empleado;

    @Column(name = "cantidad")
    private BigDecimal cantidad;

    @Column(name = "fue_chofer")
    private Boolean chofer;

    // Relación: Un ayudante pertenece a un chofer (Este es el lado INVERSO)
    // La FK estará en esta misma tabla.
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}) // LAZY es común para relaciones padre
    @JoinColumn(name = "chofer_origen_id") // Nombre de la columna FK para el chofer
    @JsonBackReference
    private LiquidacionTotalizadoEmpleadoEntity choferOrigen;

    // Relación: Un chofer tiene muchos ayudantes (Este es el lado PROPIETARIO)
    // La FK está en la tabla del ayudante (esta misma), en la columna referenciada por joinColumn.
    @OneToMany(mappedBy = "choferOrigen", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    // mappedBy apunta al atributo en la entidad hija (ayudante) que posee la FK
    @JsonManagedReference
    private List<LiquidacionTotalizadoEmpleadoEntity> ayudantes = new LinkedList<>();

    @ManyToOne( fetch = FetchType.LAZY)
    @JoinColumn(name = "origen_totalizado_id")
    private LiquidacionTotalizadoEntity origenTotalizado;

    // Métodos helper para mantener la consistencia de la relación bidireccional
    public void addAyudante(LiquidacionTotalizadoEmpleadoEntity ayudante) {
        ayudantes.add(ayudante);
        ayudante.setChoferOrigen(this); // Establece la relación inversa
    }

    public void removeAyudante(LiquidacionTotalizadoEmpleadoEntity ayudante) {
        ayudantes.remove(ayudante);
        ayudante.setChoferOrigen(null); // Rompe la relación inversa
    }
    
    public void setOrigenTotalizado(LiquidacionTotalizadoEntity totalizado) {
        this.origenTotalizado = totalizado;
        // Si el totalizado no tiene esta empleado, agregarlo
        if (totalizado != null && !totalizado.getEmpleadoTotalizadoLiquidacion().contains(this)) {
            totalizado.getEmpleadoTotalizadoLiquidacion().add(this);
        }
    }
    // hashCode e equals basados en 'id' si se usa en colecciones
    // ... (implementación estándar)
    @PrePersist
    public void prePersist() {
        if(ObjectUtils.isEmpty(chofer)){
        chofer = false;
        }
//        origenBitacora = false;
//        totalAbsoluto = totalAbsoluto();
    }
}
