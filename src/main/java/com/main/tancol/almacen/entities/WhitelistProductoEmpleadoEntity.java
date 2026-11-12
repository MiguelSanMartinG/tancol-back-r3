package com.main.tancol.almacen.entities;

import com.main.tancol.common.entities.base.BaseEntity;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.producto.entities.insumos.ProductoBasicoEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "whitelist_producto_empleado")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WhitelistProductoEmpleadoEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private EmpleadosEntity empleado;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_basico_id", nullable = false)
    private ProductoBasicoEntity producto;
}
