package com.main.tancol.medidores.entities;

import com.main.tancol.almacen.entities.UnidadesMedidaEntity;
import com.main.tancol.common.entities.base.BaseEntity;
import com.main.tancol.medidores.enums.TiposAgua;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "medidores")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedidoresEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID idMedidor;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "medidor_padre_id_medidor")
    private MedidoresEntity medidorPadre;

    @OneToMany(mappedBy = "medidorPadre", cascade = CascadeType.ALL)
    private Set<MedidoresEntity> medidoresHijos = new LinkedHashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_agua")
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private TiposAgua tipoAgua;

    @Column(name = "medicion_actual", precision = 19, scale = 2)
    private BigDecimal medicion;
    
    @ManyToMany
    @JoinTable(
            name = "medidores_sistemas_almacenamiento",
            joinColumns = @JoinColumn(name = "medidor_id_medidor"),
            inverseJoinColumns = @JoinColumn(name = "sistema_de_almacenamiento_id")
    )
    private Set<SistemasDeAlmacenamientoEntity> sistemasDeAlmacenamiento = new LinkedHashSet<>();

    @ManyToOne
    @JoinColumn(name = "unidades_medida_id_unidades_medida")
    private UnidadesMedidaEntity unidadadMedidaDefault;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "alias")
    private String alias;

    /** Consumo diario en litros a partir del cual se avisa al capturar la lectura. Null = sin alerta. */
    @Column(name = "alerta_consumo_litros", precision = 19, scale = 2)
    private BigDecimal alertaConsumoLitros;

    @Column(name = "fecha_ultima_medicion")
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private LocalDateTime fechaUltimaMedicion;

    public void resetearMedidor(){
        this.setMedicion(new BigDecimal(0));
    }

    public boolean tienePadre(){
        return this.getMedidorPadre() != null;
    }






// Agrega m??s campos aqu??
}
