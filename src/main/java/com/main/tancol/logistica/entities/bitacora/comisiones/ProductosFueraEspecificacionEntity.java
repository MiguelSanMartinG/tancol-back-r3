package com.main.tancol.logistica.entities.bitacora.comisiones;

import com.main.tancol.logistica.entities.bitacora.VentaTrayectoEntity;
import com.main.tancol.producto.entities.ConceptosEntity;
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
    @Table(name = "productos_fuera_especificacion")
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class ProductosFueraEspecificacionEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @JdbcTypeCode(SqlTypes.UUID)
        private UUID idPFE;

        @ManyToOne
        @JoinColumn(name = "concepto_id")
        private ConceptosEntity concepto;

        @Column(name = "cantidad", precision = 19, scale = 2)
        private BigDecimal cantidad;

        @ManyToOne
        @JoinColumn(name = "venta_trayecto_entity_id_venta")
        private VentaTrayectoEntity ventaOrigen;

        @Column(name = "createdAt", updatable = false)
        @JdbcTypeCode(SqlTypes.TIMESTAMP)
        private LocalDateTime createdAt;

        @Column(name = "updatedAt")
        @JdbcTypeCode(SqlTypes.TIMESTAMP)
        private LocalDateTime updatedAt;

        @PrePersist
        public void prePersist() {
            updatedAt = LocalDateTime.now();
            createdAt = LocalDateTime.now();
        }

        @PreUpdate
        public void preUpdate() {
            updatedAt = LocalDateTime.now();
        }

    // Agrega m??s campos aqu??

        @Override
        public final boolean equals(Object o) {
            if (this == o) return true;
            if (o == null) return false;
            Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
            Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
            if (thisEffectiveClass != oEffectiveClass) return false;
            ProductosFueraEspecificacionEntity that = (ProductosFueraEspecificacionEntity) o;
            return getIdPFE() != null && Objects.equals(getIdPFE(), that.getIdPFE());
        }

        @Override
        public final int hashCode() {
            return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
        }
    }

