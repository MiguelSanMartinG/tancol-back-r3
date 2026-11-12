package com.main.tancol.cliente.entities;

import com.main.tancol.common.entities.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "tipos_clientes")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TipoClienteEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(name = "nombre")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String nombre;

    @Column(name = "alias")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String alias;

    @OneToMany(mappedBy = "tipoDeCliente", cascade = CascadeType.ALL)
    @OrderBy("createdAt DESC")
    private List<ClienteEntity> clientes = new ArrayList<>();



// Agrega m??s campos aqu??
}
