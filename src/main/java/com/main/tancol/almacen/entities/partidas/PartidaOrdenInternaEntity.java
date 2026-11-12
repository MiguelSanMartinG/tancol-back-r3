package com.main.tancol.almacen.entities.partidas;

import com.main.tancol.almacen.entities.AlmacenEntity;
import com.main.tancol.almacen.entities.OrdenInternaEntity;
import com.main.tancol.almacen.entities.UnidadesMedidaEntity;
import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;


@Entity
@Table(name = "partida_orden_interna")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartidaOrdenInternaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID idPartida;

    @ManyToOne
    @JoinColumn(name = "orden_interna_id")
    private OrdenInternaEntity ordenInternaEntity;

    @ManyToOne
    @JoinColumn(name = "almacen_id")
    private AlmacenEntity almacenEntity;

    @ManyToOne
    @JoinColumn(name = "producto_basico_id")
    private ProductoBasicoEntity productoBasicoEntity;

    @Column(name = "cantidad", precision = 19, scale = 2)
    private BigDecimal cantidad;

    @ManyToOne
    @JoinColumn(name = "unidades_medida_id")
    private UnidadesMedidaEntity unidadesMedidaEntity;

    @Column(name = "comentario")
    private String comentario;

    // Agrega m??s campos aqu??
}
