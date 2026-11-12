package com.main.tancol.permisos.repositories;

import com.main.tancol.permisos.entities.ProfileEntity;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import java.util.*;

@Repository
public interface ProfileRepository extends CrudRepository<ProfileEntity, UUID>, JpaSpecificationExecutor<ProfileEntity>{
    Optional<ProfileEntity> findByRoleIdAndModuleName(Long id, String moduleName);
    // Agrega m??todos personalizados aqu??
}
