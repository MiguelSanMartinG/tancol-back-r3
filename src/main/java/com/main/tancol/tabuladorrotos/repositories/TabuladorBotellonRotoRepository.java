package com.main.tancol.tabuladorrotos.repositories;

import com.main.tancol.tabuladorrotos.entities.TabuladorBotellonRotoEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TabuladorBotellonRotoRepository extends CrudRepository<TabuladorBotellonRotoEntity, UUID>,
        JpaSpecificationExecutor<TabuladorBotellonRotoEntity> {

    boolean existsByEmpleado_IdAndProducto_IdAndDeletedFalse(UUID empleadoId, UUID productoId);

    boolean existsByEmpleado_IdAndProducto_IdAndDeletedFalseAndIdNot(UUID empleadoId, UUID productoId, UUID id);

    Optional<TabuladorBotellonRotoEntity> findByEmpleado_IdAndProducto_IdAndDeletedFalseAndEnabledTrue(
            UUID empleadoId, UUID productoId);
}
