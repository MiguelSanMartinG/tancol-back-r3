package com.main.tancol.almacen.repositories;

import com.main.tancol.almacen.entities.partidas.PartidaTransferenciaEntity;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import java.util.*;

@Repository
public interface PartidaTransferenciaRepository extends CrudRepository<PartidaTransferenciaEntity, UUID>, JpaSpecificationExecutor<PartidaTransferenciaEntity>{
    // Agrega m??todos personalizados aqu??
}
