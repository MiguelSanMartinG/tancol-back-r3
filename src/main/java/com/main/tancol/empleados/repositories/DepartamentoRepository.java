package com.main.tancol.empleados.repositories;

import com.main.tancol.empleados.entities.DepartamentoEntity;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface DepartamentoRepository extends CrudRepository<DepartamentoEntity, UUID>, JpaSpecificationExecutor<DepartamentoEntity> {
}
