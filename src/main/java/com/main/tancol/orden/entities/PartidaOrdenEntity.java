package com.main.tancol.orden.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.main.tancol.almacen.entities.Almacen;
import com.main.tancol.producto.entities.ProductoEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Entity
@Table
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    private BigDecimal iva;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "producto_id")
    private ProductoEntity productoEntity;

    @ManyToOne
    @JoinColumn(name = "solicitud_compra_entity_id")
    private OrdenEntity solicitudCompraEntity;

    @Column(name = "unidades_recibidas")
    private Integer unidadesRecibidas;

    @Column(name = "comentarios_partida")
    private String comentario;

    @ManyToOne
    @JoinColumn(name = "almacen")
    private Almacen almacen;

}
