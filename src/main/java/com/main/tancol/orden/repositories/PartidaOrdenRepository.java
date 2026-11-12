package com.main.tancol.orden.repositories;

import com.main.tancol.orden.entities.PartidaOrdenEntity;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import java.util.*;

@Repository
public interface PartidaOrdenRepository extends CrudRepository<PartidaOrdenEntity, UUID>, JpaSpecificationExecutor<PartidaOrdenEntity>{
    // Agrega m??todos personalizados aqu??
}
