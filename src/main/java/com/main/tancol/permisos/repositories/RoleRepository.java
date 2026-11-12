package com.main.tancol.permisos.repositories;

import com.main.tancol.permisos.entities.RoleEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<RoleEntity, Long>, JpaSpecificationExecutor<RoleEntity>{
    // Agrega m??todos personalizados aqu??
}
