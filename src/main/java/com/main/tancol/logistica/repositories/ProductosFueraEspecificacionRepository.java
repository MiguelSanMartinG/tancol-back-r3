package com.main.tancol.logistica.repositories;

import com.main.tancol.logistica.entities.bitacora.comisiones.ProductosFueraEspecificacionEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductosFueraEspecificacionRepository extends CrudRepository<ProductosFueraEspecificacionEntity, UUID>, JpaSpecificationExecutor<ProductosFueraEspecificacionEntity>{
    // Agrega m??todos personalizados aqu??
}
