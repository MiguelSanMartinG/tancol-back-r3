package com.main.tancol.cliente.entities;

import com.main.tancol.common.entities.base.BaseEntity;
import com.main.tancol.logistica.entities.rutas.RutasEntity;
import com.main.tancol.proveedor.entity.InformacionFiscal;
import com.main.tancol.proveedor.enums.TipoPago;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "clientes")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(name = "nombre_comercial")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String nombreComercial;

    private Integer idCliente;

//    @Enumerated
//    @Column(name = "tipo_contribuyente")
//    private TipoContribuyente tipoContribuyente;

    @ManyToOne
    @JoinColumn(name = "tipos_cliente_id")
    private TipoClienteEntity tipoDeCliente;

    @Column(name = "correo")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String correo;

    @Column(name = "telefono")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String telefono;

    @Column(name = "fecha_alta")
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private LocalDateTime fechaAlta;

    @Enumerated
    @Column(name = "tipo_pago")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private TipoPago tipoPago;

    @Column(name = "dias_de_credito")
    private Long diasDeCredito;

    @ManyToOne
    @JoinColumn(name = "ruta_asociada_id")
    private RutasEntity rutaAsociada;

    @Embedded
    InformacionFiscal informacionFiscal;

    @Column(name = "comentario", length = 800)
    private String comentario;

    @Column(name = "es_empleado", nullable = false)
    private boolean esEmpleado = false;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ClienteProductoEntity> clienteProductoRel = new LinkedHashSet<>();



}
