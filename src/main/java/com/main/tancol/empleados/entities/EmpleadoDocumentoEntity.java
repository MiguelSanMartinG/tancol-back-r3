package com.main.tancol.empleados.entities;

import com.main.tancol.common.entities.base.BaseEntity;
import com.main.tancol.empleados.enums.NivelEstudios;
import com.main.tancol.empleados.enums.TipoDocumentoEmpleado;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.UUID;

/** Documento adjunto del expediente de RH de un empleado (uno por tipo). */
@Entity
@Table(name = "empleado_documentos")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoDocumentoEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private EmpleadosEntity empleado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 40)
    private TipoDocumentoEmpleado tipo;

    /** Nombre del archivo guardado por POST /empleados/documentos/archivo. */
    @Column(name = "archivo", nullable = false, length = 300)
    private String archivo;

    /** Fecha de emisión o de inicio, según el tipo. */
    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "fecha_vigencia")
    private LocalDate fechaVigencia;

    /** Mes del documento, formato yyyy-MM (constancia fiscal, comprobante de domicilio). */
    @Column(name = "mes", length = 7)
    private String mes;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_estudios", length = 20)
    private NivelEstudios nivelEstudios;

    @Column(name = "banco", length = 100)
    private String banco;

    @Column(name = "clabe", length = 18)
    private String clabe;
}
