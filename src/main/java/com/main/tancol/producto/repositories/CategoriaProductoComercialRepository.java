package com.main.tancol.producto.repositories;

import com.main.tancol.producto.entities.comerciales.CategoriaProductoComercialEntity;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import java.util.*;

@Repository
public interface CategoriaProductoComercialRepository extends CrudRepository<CategoriaProductoComercialEntity, UUID>, JpaSpecificationExecutor<CategoriaProductoComercialEntity>{
    // Agrega m??todos personalizados aqu??
}
