package com.main.tancol.logistica.entities.bitacora;

import com.main.tancol.logistica.enums.EstatusBitacoraVenta;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "bitacora_rutas_ventas")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BitacoraVentaRutaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID idBitacora;

    @Column
    private String folio;

    @Enumerated(EnumType.STRING)
    @Column(name = "estatus_bitacora_venta")
    private EstatusBitacoraVenta estatusBitacoraVenta;

    @Column(name = "fecha", updatable = false)
    @JdbcTypeCode(SqlTypes.DATE)
    private LocalDate fecha;


    @Column(name = "createdAt", updatable = false)
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "bitacoraOrigen", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TrayectoEntity> trayectos = new LinkedHashSet<>();


    public void removerPartida(TrayectoEntity partida) {
        partida.setBitacoraOrigen(null);
        trayectos.remove(partida);

    }

    public void agregarPartida(TrayectoEntity partida) {
        partida.setBitacoraOrigen(this);
        trayectos.add(partida);
    }

    @PrePersist
    public void prePersist() {
        fecha = LocalDate.now();
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
        BitacoraVentaRutaEntity that = (BitacoraVentaRutaEntity) o;
        return getIdBitacora() != null && Objects.equals(getIdBitacora(), that.getIdBitacora());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
