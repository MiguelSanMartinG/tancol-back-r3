package com.main.tancol.solicitudes.entities;

import com.main.tancol.almacen.entities.UnidadesMedidaEntity;
import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "solicitudes_compra_partidas")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudPartidaEntity {

    @ManyToOne
    @JoinColumn(name = "padre_id")
    private SolicitudEntity padre;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID idPartida;

    @Column(name = "cantidad", precision = 19, scale = 2)
    private BigDecimal cantidad;

    @ManyToOne
    @JoinColumn(name = "producto_insumo_id")
    private ProductoBasicoEntity productoInsumo;

    @ManyToOne
    @JoinColumn(name = "unidad_de_medida_id_unidades_medida")
    private UnidadesMedidaEntity unidadDeMedida;

}
