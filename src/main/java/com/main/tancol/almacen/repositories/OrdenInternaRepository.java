package com.main.tancol.almacen.repositories;

import com.main.tancol.almacen.entities.OrdenInternaEntity;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import java.util.*;

@Repository
public interface OrdenInternaRepository extends CrudRepository<OrdenInternaEntity, UUID>, JpaSpecificationExecutor<OrdenInternaEntity>{
    // Agrega m??todos personalizados aqu??
}
