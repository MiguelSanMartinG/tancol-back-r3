package com.main.tancol.permisos.entities;

import com.main.tancol.common.entities.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;


@Entity
@Table(name = "perfiles")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID idPerfil;

    @ManyToOne
    @JoinColumn(name = "module_id")
    private ModuleEntity module;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private RoleEntity role;

    private boolean canRead;
    private boolean canWrite;
    private boolean canDelete;
    private boolean canUpdate;

// Agrega m??s campos aqu??
}
