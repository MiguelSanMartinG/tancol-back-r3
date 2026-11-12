package com.main.tancol.producto.entities.insumos;

import com.main.tancol.almacen.entities.InventarioEntity;
import com.main.tancol.almacen.entities.UnidadesMedidaEntity;
import com.main.tancol.common.entities.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ToString(callSuper = true)
@Getter
@Setter
@Entity
@Slf4j
@NoArgsConstructor
@Table(name = "productos_basicos")
@SuperBuilder
public class ProductoBasicoEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    @NotBlank(message = "Debe tener un nombre")
    @Column(name = "nombre", unique = true, nullable = false)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String nombre;

    @Column(name = "id_imagen")
    private String idImagen;

    @Column(name = "descripcion", length = 510)
    private String descripcion;

    @Column(name="SKU")
    private String sku;

    @Column
    private String alias;

    @Column(name = "marca")
    private String marca;

    @Column(name = "modelo")
    private String modelo;

    @Column(name = "stock_minimo")
    private BigDecimal stockMinimo;

    @Column(name = "stock_maximo")
    private BigDecimal stockMaximo;

    @Builder.Default
    @Column(name = "notificar_stock_minimo", nullable = false)
    private boolean notificarStockMinimo = false;

    @Builder.Default
    @Column(name = "tiene_vigencia", nullable = false)
    private boolean tieneVigencia = false;

    @Column(name = "fecha_vigencia")
    private LocalDate fechaVigencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "categoria_producto_id")
    private CategoriaProductoEntity categoriaProductoEntity;

    @OneToMany(mappedBy = "productoBasicoEntity", orphanRemoval = false)
    private List<InventarioEntity> inventarioEntities = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "unidad_medida_default_id_unidades_medida")
    private UnidadesMedidaEntity unidadMedidaDefault;

}
