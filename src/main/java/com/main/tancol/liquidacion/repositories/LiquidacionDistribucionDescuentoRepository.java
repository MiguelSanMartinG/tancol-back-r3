package com.main.tancol.liquidacion.repositories;

import com.main.tancol.liquidacion.entities.LiquidacionDistribucionDescuentoEntity;
import com.main.tancol.liquidacion.enums.EstadosLiquidacion;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LiquidacionDistribucionDescuentoRepository extends CrudRepository<LiquidacionDistribucionDescuentoEntity, UUID>, JpaSpecificationExecutor<LiquidacionDistribucionDescuentoEntity>{
    List<LiquidacionDistribucionDescuentoEntity> findByLiquidacionPartidaEntity_LiquidacionOrigen_EstadosLiquidacion(EstadosLiquidacion estadosLiquidacion);
    // Agrega m??todos personalizados aqu??

}
