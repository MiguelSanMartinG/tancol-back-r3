package com.main.tancol.cuadreinventario.entities;

import com.main.tancol.almacen.entities.InventarioEntity;
import com.main.tancol.common.entities.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Renglón del cuadre: un registro de inventario del almacén. El producto, SKU, unidad y la
 * cantidad del sistema se copian al abrir el cuadre para que el registro no cambie después.
 */
@Entity
@Table(name = "cuadres_inventario_partidas")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CuadreInventarioPartidaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cuadre_id", nullable = false)
    private CuadreInventarioEntity cuadre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventario_id")
    private InventarioEntity inventario;

    @Column(name = "producto_id")
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID productoId;

    @Column(name = "producto", length = 255)
    private String producto;

    @Column(name = "sku", length = 255)
    private String sku;

    @Column(name = "unidad", length = 255)
    private String unidad;

    @Column(name = "cantidad_sistema", precision = 19, scale = 2, nullable = false)
    private BigDecimal cantidadSistema;

    @Column(name = "cantidad_fisica", precision = 19, scale = 2)
    private BigDecimal cantidadFisica;

    public BigDecimal getDiferencia() {
        return cantidadFisica == null ? null : cantidadFisica.subtract(cantidadSistema);
    }
}
