package com.main.tancol.cliente.repositories;

import com.main.tancol.cliente.entities.ClienteProductoEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface ClienteProductoRelRepository extends CrudRepository<ClienteProductoEntity, UUID>, JpaSpecificationExecutor<ClienteProductoEntity>{
    // Agrega m??todos personalizados aqu??
    Optional<ClienteProductoEntity> findByCliente_IdAndProductoComercial_Id(UUID id, UUID id1);

    Optional<ClienteProductoEntity> findFirstByCliente_IdAndProductoComercial_Id(UUID id, UUID id1);

    List<ClienteProductoEntity> findByCliente_Id(UUID id);
}
