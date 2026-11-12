package com.main.tancol.dashboard.entities;

import com.main.tancol.common.entities.base.BaseEntity;
import com.main.tancol.dashboard.dto.DashboardKpiPreferenciasDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(
        name = "dashboard_kpi_configuracion",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_dashboard_kpi_configuracion_empleado",
                columnNames = "empleado_id"))
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardKpiConfiguracionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(name = "empleado_id", nullable = false, unique = true)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID empleadoId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "configuracion", nullable = false, columnDefinition = "jsonb")
    private DashboardKpiPreferenciasDTO configuracion;
}
