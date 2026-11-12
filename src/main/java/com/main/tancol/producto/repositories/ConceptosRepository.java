package com.main.tancol.producto.repositories;

import com.main.tancol.producto.entities.ConceptosEntity;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import java.util.*;

@Repository
public interface ConceptosRepository extends CrudRepository<ConceptosEntity, Long>, JpaSpecificationExecutor<ConceptosEntity>{
    // Agrega m??todos personalizados aqu??
}
