package com.main.tancol.almacen.entities.partidas;

import com.main.tancol.almacen.entities.AlmacenEntity;
import com.main.tancol.almacen.entities.TransferenciasEntity;
import com.main.tancol.almacen.entities.UnidadesMedidaEntity;
import com.main.tancol.common.entities.base.BaseEntity;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;


@Entity
@Table(name = "partidas_transferencia_material")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartidaTransferenciaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID idPartida;

    @Column(name = "comentarios")
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String comentarios;

    @ManyToOne
    @JoinColumn(name = "almacen_origen_id")
    private AlmacenEntity almacenOrigen;

    @ManyToOne
    @JoinColumn(name = "almacen_destino_id")
    private AlmacenEntity almacenDestino;

    @Column(name = "cantidades", precision = 19, scale = 2)
    @JdbcTypeCode(SqlTypes.BIGINT)
    private BigDecimal cantidades;

    @ManyToOne
    @JoinColumn(name = "producto_basico_entity_id")
    private ProductoBasicoEntity productoBasicoEntity;

    private String motivo;

    @ManyToOne
    @JoinColumn(name = "empleados_entity_id")
    private EmpleadosEntity empleadosEntity;

    @ManyToOne
    @JoinColumn(name = "id_transferencia_padre")
    private TransferenciasEntity padrePartida;

    @ManyToOne
    @JoinColumn(name = "unidad_medida_id")
    private UnidadesMedidaEntity unidadesMedidaEntity;

}
