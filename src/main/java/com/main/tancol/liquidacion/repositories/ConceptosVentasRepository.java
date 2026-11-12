package com.main.tancol.liquidacion.repositories;

import com.main.tancol.liquidacion.entities.ConceptosVentasEntity;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import java.util.*;

@Repository
public interface ConceptosVentasRepository extends CrudRepository<ConceptosVentasEntity, UUID>, JpaSpecificationExecutor<ConceptosVentasEntity>{
    // Agrega m??todos personalizados aqu??
}
