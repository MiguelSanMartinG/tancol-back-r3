package com.main.tancol.almacen.repositories;

import com.main.tancol.almacen.entities.AlmacenConfiguracionEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AlmacenConfiguracionRepository extends CrudRepository<AlmacenConfiguracionEntity, UUID> {
}
