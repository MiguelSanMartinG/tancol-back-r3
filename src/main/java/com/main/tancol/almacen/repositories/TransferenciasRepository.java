package com.main.tancol.almacen.repositories;

import com.main.tancol.almacen.entities.TransferenciasEntity;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import java.util.*;

@Repository
public interface TransferenciasRepository extends CrudRepository<TransferenciasEntity, UUID>, JpaSpecificationExecutor<TransferenciasEntity>{
    // Agrega m??todos personalizados aqu??
}
