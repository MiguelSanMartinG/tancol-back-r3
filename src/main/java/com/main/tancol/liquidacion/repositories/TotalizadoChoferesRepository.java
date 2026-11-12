package com.main.tancol.liquidacion.repositories;

import com.main.tancol.liquidacion.entities.LiquidacionTotalizadoEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TotalizadoChoferesRepository extends CrudRepository<LiquidacionTotalizadoEntity, UUID>, JpaSpecificationExecutor<LiquidacionTotalizadoEntity>{
    // Agrega m??todos personalizados aqu??
}
