package com.main.tancol.empleados.repositories;

import com.main.tancol.empleados.entities.TiposEmpleadosEntity;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import java.util.*;

@Repository
public interface TiposEmpleadosRepository extends CrudRepository<TiposEmpleadosEntity, UUID>, JpaSpecificationExecutor<TiposEmpleadosEntity>{
    // Agrega m??todos personalizados aqu??
}
