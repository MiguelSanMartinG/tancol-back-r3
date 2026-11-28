package com.main.tancol.cuadreinventario.entities;

import com.main.tancol.almacen.entities.AlmacenEntity;
import com.main.tancol.common.entities.base.BaseEntity;
import com.main.tancol.cuadreinventario.enums.EstadoCuadre;
import com.main.tancol.cuadreinventario.enums.TipoCuadre;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.producto.entities.insumos.CategoriaProductoEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Cuadre de inventario de un almacén: foto de la existencia del sistema al abrirlo, la cantidad
 * física contada por producto y el cierre con firma. Cerrado ya no se modifica.
 */
@Entity
@Table(name = "cuadres_inventario")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CuadreInventarioEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(name = "folio", length = 20)
    private String folio;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_id", nullable = false)
    private AlmacenEntity almacen;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private EstadoCuadre estado = EstadoCuadre.ABIERTO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por_id")
    private EmpleadosEntity creadoPor;

    @Column(name = "fecha_creacion")
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private LocalDateTime fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cerrado_por_id")
    private EmpleadosEntity cerradoPor;

    @Column(name = "fecha_cierre")
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private LocalDateTime fechaCierre;

    @Column(name = "ajuste_aplicado", nullable = false)
    private boolean ajusteAplicado;

    @Column(name = "observaciones", length = 1000)
    private String observaciones;

    @Column(name = "id_imagen_firma")
    private String idImagenFirma;

    @Column(name = "fecha_firma")
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private LocalDateTime fechaFirma;

    @OneToMany(mappedBy = "cuadre", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("producto ASC")
    @Builder.Default
    private List<CuadreInventarioPartidaEntity> partidas = new ArrayList<>();

    /** Periodicidad del conteo. Null en cuadres anteriores a este campo. */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", length = 20)
    private TipoCuadre tipo;

    /** Categorías elegidas al crear el cuadre (sin expandir subcategorías). Vacío = todo el almacén. */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "cuadre_inventario_categorias",
            joinColumns = @JoinColumn(name = "cuadre_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id"))
    @BatchSize(size = 50)
    @Builder.Default
    private Set<CategoriaProductoEntity> categorias = new LinkedHashSet<>();
}
