package com.main.tancol.almacen.repositories;

import com.main.tancol.almacen.entities.partidas.PartidaOrdenInternaEntity;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import java.util.*;

@Repository
public interface PartidaOrdenInternaRepository extends CrudRepository<PartidaOrdenInternaEntity, UUID>, JpaSpecificationExecutor<PartidaOrdenInternaEntity>{
    // Agrega m??todos personalizados aqu??
}
