package com.main.tancol.proveedor.entity;

import com.main.tancol.common.entities.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;


@Entity
@Getter
@Setter
@SuperBuilder
@Table(name = "tipo_impuesto")
public class TipoImpuestoEntity extends BaseEntity {

    public TipoImpuestoEntity(){
        super();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    @Column(name = "nombre")
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String nombre;

    @Column(name = "porcentaje")
    @JdbcTypeCode(SqlTypes.BIGINT)
    private BigDecimal porcentaje;

}
