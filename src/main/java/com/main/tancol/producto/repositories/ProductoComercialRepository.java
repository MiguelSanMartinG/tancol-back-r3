package com.main.tancol.producto.repositories;

import com.main.tancol.producto.entities.comerciales.ProductoComercialEntity;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import java.util.*;

@Repository
public interface ProductoComercialRepository extends CrudRepository<ProductoComercialEntity, UUID>, JpaSpecificationExecutor<ProductoComercialEntity>{
    void findByNombre(@NotNull(message = "Debe tener un nombre") String nombre);

    boolean existsByNombre(String nombre);     // Agrega m??todos personalizados aqu??
}
