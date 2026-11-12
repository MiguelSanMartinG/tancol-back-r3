package com.main.tancol.liquidacion.entities;

import com.main.tancol.liquidacion.enums.EstadosLiquidacion;
import com.main.tancol.logistica.entities.rutas.RutasEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;


@Entity
@Table(name = "liquidacion")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LiquidacionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID idLiquidacion;

    @Version
    private Integer version;

    // --- Información General ---

    @Column(name = "folio")
    private String folio;

    @ManyToOne
    @JoinColumn(name = "ruta_id")
    private RutasEntity ruta;

    private LocalDateTime fecha;

    @Column(name = "bloque_nota")
    private String bloqueNota;

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    @Enumerated(EnumType.STRING) //    PENDIENTE, BORRADOR, FINALIZADA
    @Column(name = "estados_liquidacion")
    private EstadosLiquidacion estadosLiquidacion;

    @Column(name = "is_from_bitacora")
    private boolean origenBitacora;

    // --- Información Choferes Ayudantes

    @OneToMany(mappedBy = "liquidacionOrigen", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LiquidacionTotalizadoEntity> totalesPorProductos = new LinkedList<>();

    public void removerTotalizado(LiquidacionTotalizadoEntity totalizado){
        this.totalesPorProductos.remove(totalizado);
        totalizado.setLiquidacionOrigen(null);
    }

    public void agregarTotalizado(LiquidacionTotalizadoEntity totalizado){
        this.totalesPorProductos.add(totalizado);
        totalizado.setLiquidacionOrigen(this);
    }
    public void clearTotalizados(){
//        this.getTotalesPorProductos().forEach(this::removerTotalizado);
        List<LiquidacionTotalizadoEntity> totalizados = new LinkedList<>(this.getTotalesPorProductos());
        totalizados.forEach(this::removerTotalizado);

    }

    // --- Partidas
    @OneToMany(mappedBy = "liquidacionOrigen", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LiquidacionPartidaEntity> liquidacionPartida = new LinkedList<>();

    public void removerNota(LiquidacionPartidaEntity nota){
        this.liquidacionPartida.remove(nota);
        nota.setLiquidacionOrigen(null);
    }

    public void agregarNota(LiquidacionPartidaEntity nota){
        this.liquidacionPartida.add(nota);
        nota.setLiquidacionOrigen(this);
    }

    public void clearNotas(){
        List<LiquidacionPartidaEntity> totalizados = new ArrayList<>(this.getLiquidacionPartida());
        totalizados.forEach(this::removerNota);
//        this.getLiquidacionPartida().forEach(this::removerNota);

    }

    // --- Total Productos

    @Column(name = "total_liquidado", precision = 19, scale = 2)
    private BigDecimal totalLiquidado = BigDecimal.ZERO;

    @Column(name = "motivo_cancelacion", length = 500)
    private String motivoCancelacion;

    @Column(name = "usuario_cancelacion")
    private String usuarioCancelacion;

    @Column(name = "fecha_cancelacion")
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private LocalDateTime fechaCancelacion;

//    @Column(name = "comentario")
//    private String comentario;

// Comunes

    @Column(name = "createdAt", updatable = false)
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private LocalDateTime updatedAt;
    private boolean deleted;


    @PrePersist
    public void prePersist() {
        updatedAt = LocalDateTime.now();
        createdAt = LocalDateTime.now();
        deleted = false;
//        origenBitacora = false;
//        totalAbsoluto = totalAbsoluto();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
//        totalAbsoluto = totalAbsoluto();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        LiquidacionEntity that = (LiquidacionEntity) o;
        return getIdLiquidacion() != null && Objects.equals(getIdLiquidacion(), that.getIdLiquidacion());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "idLiquidacion = " + idLiquidacion + ", " +
                "version = " + version + ", " +
                "folio = " + folio + ", " +
                "ruta = " + ruta + ", " +
                "fecha = " + fecha + ", " +
                "bloqueNota = " + bloqueNota + ", " +
                "observaciones = " + observaciones + ", " +
                "estadosLiquidacion = " + estadosLiquidacion + ", " +
                "totalLiquidado = " + totalLiquidado + ", " +
                "createdAt = " + createdAt + ", " +
                "updatedAt = " + updatedAt + ", " +
                "deleted = " + deleted + ")";
    }

    //    @OneToMany(mappedBy = "liquidacionPadre", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<LiquidacionVentaPartidaEntity> notas = new HashSet<>();



    // Calculadas

//    @Column(name = "total_absoluto", precision = 19, scale = 2)
//    private BigDecimal totalAbsoluto;
//
//    public BigDecimal totalAbsoluto(){
//        throw new RuntimeException("TBA");
//    }

}
