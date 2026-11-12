package com.main.tancol.cliente.repositories;

import com.main.tancol.cliente.entities.ClienteNombreHistorialEntity;
import com.main.tancol.cliente.enums.TipoNombreHistorial;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClienteNombreHistorialRepository extends CrudRepository<ClienteNombreHistorialEntity, UUID> {

    List<ClienteNombreHistorialEntity> findByClienteIdAndTipoOrderByFechaDesc(UUID clienteId, TipoNombreHistorial tipo);

    Optional<ClienteNombreHistorialEntity> findFirstByClienteIdAndTipoOrderByFechaDesc(UUID clienteId, TipoNombreHistorial tipo);

    List<ClienteNombreHistorialEntity> findByClienteIdInOrderByFechaDesc(Collection<UUID> clienteIds);
}
