package com.main.tancol.proveedor.entity;

import com.main.tancol.common.entities.base.BaseEntity;
import com.main.tancol.producto.entities.PresentacionProductoBasico;
import com.main.tancol.proveedor.enums.TipoPago;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Slf4j
@Table(name = "proveedores")
@SuperBuilder
public class ProveedorEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, name = "id")
    private UUID id;

    @NotNull(message = "Debe tener un nombre")
    @Column(name = "nombre_comercial")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String nombreComercial;

    @ManyToMany
    @JoinTable(
            name = "proveedor_tipos",
            joinColumns = @JoinColumn(name = "proveedor_id"),
            inverseJoinColumns = @JoinColumn(name = "tipo_proveedor_id")
    )
    @Builder.Default
    private Set<TipoProveedorEntity> tiposProveedor = new LinkedHashSet<>();

    @Enumerated
    @Column(name = "tipo_pago")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private TipoPago condicionPago;

    @Column(name = "dias_de_credito")
    private Long diasDeCredito;

    @Column(name = "correo")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String correo;

    @Column(name = "telefono")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String telefono;

    @Embedded
    InformacionFiscal informacionFiscal;

    @OneToMany(mappedBy = "proveedorEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PresentacionProductoBasico> productosProveedoreEntities = new LinkedHashSet<>();

}
