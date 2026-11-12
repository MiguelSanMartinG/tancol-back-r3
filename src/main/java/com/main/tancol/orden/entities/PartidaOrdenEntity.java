package com.main.tancol.orden.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.main.tancol.almacen.entities.AlmacenEntity;
import com.main.tancol.almacen.entities.UnidadesMedidaEntity;
import com.main.tancol.producto.entities.PresentacionProductoBasico;
import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import com.main.tancol.proveedor.entity.ProveedorEntity;
import com.main.tancol.proveedor.entity.TipoImpuestoEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Slf4j
@Table(name = "partidas_orden")
public class PartidaOrdenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio",precision = 21, scale = 8)
    private BigDecimal precioUnitario;

    @Column(name = "iva",precision = 21, scale = 8)
    private BigDecimal impuestoAplicado;

    @Column(name = "descuento")
    private BigDecimal descuentoAplicado;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "producto_id")
    private ProductoBasicoEntity producto;

    @ManyToOne
    @JoinColumn(name = "solicitud_compra_entity_id")
    private OrdenEntity solicitudCompraEntity;


    @Column(name = "unidades_recibidas")
    private Integer unidadesRecibidas;

    @Column(name = "comentarios_partida")
    private String comentario;

    @Builder.Default
    @Column(name = "verificado")
    private Boolean verificado = Boolean.FALSE;

    @ManyToOne
    @JoinColumn(name = "almacen")
    private AlmacenEntity almacen;

    @ManyToOne
    @JoinColumn(name= "tipo_impuesto")
    private TipoImpuestoEntity impuestoRelacion;

    @ToString.Exclude
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "precio_proveedor_entity_id")
    private PresentacionProductoBasico precioRelacion;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "proveedor_id")
    private ProveedorEntity proveedor;

    @ManyToOne
    @JoinColumn(name = "unidades_medida_id_unidades_medida")
    private UnidadesMedidaEntity unidadesMedida;

}
