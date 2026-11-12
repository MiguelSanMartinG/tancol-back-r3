package com.main.tancol.liquidacion.repositories;

import com.main.tancol.liquidacion.entities.ReparticionEntreCuentasEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReparticionEntreCuentasRepository extends CrudRepository<ReparticionEntreCuentasEntity, UUID>, JpaSpecificationExecutor<ReparticionEntreCuentasEntity>{
    ReparticionEntreCuentasEntity findTopByOrderByCreatedAtDesc();
//    Optional<ReparticionEntreCuentasEntity> getFirstByCreatedAt();

    // Agrega m??todos personalizados aqu??
}
