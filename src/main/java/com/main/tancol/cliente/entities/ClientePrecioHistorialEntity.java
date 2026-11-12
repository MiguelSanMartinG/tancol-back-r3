package com.main.tancol.cliente.entities;

import com.main.tancol.cliente.enums.TipoActualizacionPrecio;
import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cliente_precio_historial")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientePrecioHistorialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(name = "operacion_id", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID operacionId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteEntity cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_comercial_id")
    private ProductoComercialEntity productoComercial;

    @Column(name = "producto_nombre")
    private String productoNombre;

    @Column(name = "precio_anterior", precision = 19, scale = 2)
    private BigDecimal precioAnterior;

    @Column(name = "precio_nuevo", precision = 19, scale = 2)
    private BigDecimal precioNuevo;

    @Column(name = "usuario", nullable = false)
    private String usuario;

    @Column(name = "fecha", nullable = false)
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_actualizacion", nullable = false, length = 20)
    private TipoActualizacionPrecio tipoActualizacion;
}
