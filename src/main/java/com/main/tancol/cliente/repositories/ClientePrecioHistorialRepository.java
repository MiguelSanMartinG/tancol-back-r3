package com.main.tancol.cliente.repositories;

import com.main.tancol.cliente.entities.ClientePrecioHistorialEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClientePrecioHistorialRepository extends CrudRepository<ClientePrecioHistorialEntity, UUID> {

    List<ClientePrecioHistorialEntity> findByCliente_IdOrderByFechaDescIdDesc(UUID clienteId);
}
