package com.main.tancol.cuadreinventario.repositories;

import com.main.tancol.cuadreinventario.entities.CuadreInventarioEntity;
import com.main.tancol.cuadreinventario.enums.EstadoCuadre;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CuadreInventarioRepository extends CrudRepository<CuadreInventarioEntity, UUID>,
        JpaSpecificationExecutor<CuadreInventarioEntity> {

    boolean existsByAlmacen_IdAndEstadoAndDeletedFalse(UUID idAlmacen, EstadoCuadre estado);

    @Query(value = "SELECT nextval('cuadres_inventario_folio_seq')", nativeQuery = true)
    Long siguienteFolio();
}
