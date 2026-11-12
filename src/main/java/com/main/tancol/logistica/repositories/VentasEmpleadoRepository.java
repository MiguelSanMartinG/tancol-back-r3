package com.main.tancol.logistica.repositories;

import com.main.tancol.logistica.entities.bitacora.comisiones.VentasEmpleadoEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VentasEmpleadoRepository extends CrudRepository<VentasEmpleadoEntity, UUID>, JpaSpecificationExecutor<VentasEmpleadoEntity>{
    // Agrega m??todos personalizados aqu??
}
