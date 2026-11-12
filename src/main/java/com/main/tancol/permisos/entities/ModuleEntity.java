package com.main.tancol.permisos.entities;

import com.main.tancol.common.entities.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Slf4j
@Table(name = "modulos")
public class ModuleEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String name; // ej. "Ventas", "Inventario", "Usuarios"

    private String rutaModulo;

    @ManyToOne
    @JoinColumn(name = "parentId")
    private ModuleEntity parent;

}
