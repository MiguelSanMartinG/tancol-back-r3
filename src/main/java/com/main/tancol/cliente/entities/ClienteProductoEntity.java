package com.main.tancol.cliente.entities;

import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "cliente_producto")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteProductoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private ClienteEntity cliente;

    @ManyToOne
    @JoinColumn(name = "producto_comercial_id")
    private ProductoComercialEntity productoComercial;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(precision = 19, scale = 2)
    private BigDecimal descuento;

}
