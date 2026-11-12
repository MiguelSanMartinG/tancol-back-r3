package com.main.tancol.cliente.repositories;

import com.main.tancol.cliente.entities.TipoClienteEntity;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import java.util.*;

@Repository
public interface TiposClientesRepository extends CrudRepository<TipoClienteEntity, UUID>, JpaSpecificationExecutor<TipoClienteEntity>{
    Optional<TipoClienteEntity> findByNombreIgnoreCaseAndDeletedFalse(String nombre);
}
