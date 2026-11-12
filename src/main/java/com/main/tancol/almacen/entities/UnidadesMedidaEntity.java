package com.main.tancol.almacen.entities;

import com.main.tancol.common.entities.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;


@Entity
@Table(name = "unidades_de_medida")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UnidadesMedidaEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID idUnidadesMedida;

    @Column(name = "nombre")
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String nombre;

    @Column(name = "alias")
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String alias;

// Agrega m??s campos aqu??
}
