package com.main.tancol.almacen.repositories;

import com.main.tancol.almacen.entities.Almacen;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AlmacenRepository extends CrudRepository<Almacen, UUID>, JpaSpecificationExecutor<Almacen> {
    List<Almacen> findByNombreContaining(String keyword);

    Optional<Almacen> findByNombre(@NotNull String nombre);
}