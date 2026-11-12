package com.main.tancol.producto.entities;

import com.main.tancol.common.entities.base.BaseEntity;
import com.main.tancol.producto.enums.TipoRezago;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "producto_rezagado")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConceptosEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated
    @Column(name = "tipo_rezago")
    private TipoRezago tipoRezago;

    @Column(name = "motivo", length = 400)
    private String motivo;

    // Agrega m??s campos aqu??
}
