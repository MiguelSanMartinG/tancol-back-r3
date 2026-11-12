package com.main.tancol.logistica.entities.bitacora;

import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.logistica.entities.rutas.RutasEntity;
import com.main.tancol.logistica.entities.rutas.VehiculosEntity;
import com.main.tancol.logistica.enums.EstadoVentaRuta;
import com.main.tancol.logistica.enums.TipoProceso;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "bitacora_rutas_ventas_trayectos")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrayectoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID idTrayecto;

    @ManyToOne
    @JoinColumn(name = "ruta_id")
    private RutasEntity ruta;

    @ManyToOne
    @JoinColumn(name = "chofer_id")
    private EmpleadosEntity chofer;

    @ManyToOne
    @JoinColumn(name = "vehiculo_id")
    private VehiculosEntity vehiculo;

    @Column(name = "id_imagen_firma")
    private String idImagenFirma;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_proceso")
    private TipoProceso tipoProceso;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_venta_ruta")
    private EstadoVentaRuta estadoVentaRuta;

    @Column(name = "mandadoaliquidacion")
    private Boolean mandadoALiquidacion;

    @Column(name = "createdAt", updatable = false)
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private LocalDateTime updatedAt;

    @Column(name = "singed_at")
    private LocalDateTime fechaFirma;

    @ManyToOne
    @JoinColumn(name = "bitacora_venta_ruta_entity_id_bitacora")
    private BitacoraVentaRutaEntity bitacoraOrigen;

    @Column(name = "comentario", length = 320)
    private String comentario;

    @OneToMany(mappedBy = "trayectoOrigen", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VentaTrayectoEntity> ventaTrayecto = new LinkedHashSet<>();

    public void removerPartida(VentaTrayectoEntity partida) {
        partida.setTrayectoOrigen(null);
        ventaTrayecto.remove(partida);
    }
    public void agregarPartida(VentaTrayectoEntity partida) {
        partida.setTrayectoOrigen(this);
        ventaTrayecto.add(partida);
    }

    @PrePersist
    public void prePersist() {
        updatedAt = LocalDateTime.now();
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        TrayectoEntity that = (TrayectoEntity) o;
        return getIdTrayecto() != null && Objects.equals(getIdTrayecto(), that.getIdTrayecto());
    }
    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
