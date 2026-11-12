package com.main.tancol.logistica.entities.bitacora;

import com.main.tancol.empleados.entities.EmpleadosEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "bitacora_venta_trayecto_mermas")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MermaVentaTrayectoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_trayecto_id", nullable = false)
    private VentaTrayectoEntity ventaOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_responsable_id")
    private EmpleadosEntity empleadoResponsable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_firmante_id")
    private EmpleadosEntity empleadoFirmante;

    @Column(name = "cantidad", nullable = false, precision = 19, scale = 2)
    private BigDecimal cantidad;

    @Column(name = "roto_por_maquina", nullable = false)
    @Builder.Default
    private boolean rotoPorMaquina = false;

    @Column(name = "id_imagen_firma")
    private String idImagenFirma;

    @Column(name = "created_at", updatable = false)
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy
                ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        MermaVentaTrayectoEntity that = (MermaVentaTrayectoEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}
