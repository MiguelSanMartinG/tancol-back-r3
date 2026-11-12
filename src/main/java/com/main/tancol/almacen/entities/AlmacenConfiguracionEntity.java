package com.main.tancol.almacen.entities;

import com.main.tancol.common.entities.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "almacen_configuracion")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlmacenConfiguracionEntity extends BaseEntity {

    public static final UUID ID_GLOBAL = UUID.fromString("c4e8a1b2-7f30-4d6a-9e21-5b0c8d3f1a74");

    @Id
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(name = "usar_whitelist", nullable = false)
    private boolean usarWhitelist;
}
