package com.main.tancol.almacen.repositories;

import com.main.tancol.almacen.entities.Departamento;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface DepartamentoRepository extends CrudRepository<Departamento, UUID> {
}
