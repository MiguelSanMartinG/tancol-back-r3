package com.main.tancol.proveedor.repositories;

import com.main.tancol.proveedor.entity.TipoProveedorEntity;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import java.util.*;

@Repository
public interface TipoProveedorRepository extends CrudRepository<TipoProveedorEntity, UUID>, JpaSpecificationExecutor<TipoProveedorEntity>{
    // Agrega m??todos personalizados aqu??
}
