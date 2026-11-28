package com.main.tancol.inventariodiario.entities;

import com.main.tancol.common.entities.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Estado del cupo en un día. Se crea con la base de la tarjeta la primera vez que el día se usa
 * (ya no hay confirmación del día). consumido = solo lo firmado.
 */
@Entity
@Table(name = "inv_diario_cupo_dia")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CupoDiaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cupo_id", nullable = false)
    private CupoEntity cupo;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "inicial", nullable = false, precision = 19, scale = 2)
    private BigDecimal inicial;

    @Column(name = "ampliado", nullable = false, precision = 19, scale = 2)
    private BigDecimal ampliado;

    @Column(name = "consumido", nullable = false, precision = 19, scale = 2)
    private BigDecimal consumido;

    /** Lo que faltó al confirmar (base − inicial) porque el almacén no alcanzó. */
    @Column(name = "faltante", nullable = false, precision = 19, scale = 2)
    private BigDecimal faltante;

    public BigDecimal disponible() {
        return inicial.add(ampliado).subtract(consumido);
    }
}
