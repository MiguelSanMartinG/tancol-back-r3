package com.main.tancol.cliente.repositories;

import com.main.tancol.cliente.entities.ClienteEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface ClienteRepository extends CrudRepository<ClienteEntity, UUID>, JpaSpecificationExecutor<ClienteEntity>{
    ClienteEntity getById(UUID id);

    Set<ClienteEntity> findByClienteProductoRel_Cliente_Id(UUID id);     // Agrega m??todos personalizados aqu??
}
