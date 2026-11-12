package com.main.tancol.producto.entities.comerciales;

import com.main.tancol.almacen.entities.UnidadesMedidaEntity;
import com.main.tancol.cliente.entities.ClienteProductoEntity;
import com.main.tancol.common.entities.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "productos_comerciales")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductoComercialEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.UUID)
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

    @Column(name="alias")
    private String alias;

    @Column(name = "precio", precision = 19, scale = 2)
    private BigDecimal precio;

    @Column(name="presentacion")
    private Long presentacion;

    @ManyToOne
    @JoinColumn(name = "categoria_comercial_id")
    private CategoriaProductoComercialEntity categoriaProductoComercialEntity;

    @OneToMany(mappedBy = "productoComercial", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ClienteProductoEntity> clienteProductoRel = new LinkedHashSet<>();

    @ManyToOne
    @JoinColumn(name = "unidad_medida_default_id_unidades_medida")
    private UnidadesMedidaEntity unidadMedidaDefault;

// Agrega m??s campos aqu??
}
