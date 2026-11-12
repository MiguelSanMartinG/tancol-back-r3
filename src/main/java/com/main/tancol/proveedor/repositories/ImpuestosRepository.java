package com.main.tancol.proveedor.repositories;

import com.main.tancol.proveedor.entity.TipoImpuestoEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ImpuestosRepository extends CrudRepository<TipoImpuestoEntity,UUID>, JpaSpecificationExecutor<TipoImpuestoEntity> {
}
