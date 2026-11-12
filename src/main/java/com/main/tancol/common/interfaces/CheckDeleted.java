package com.main.tancol.common.interfaces;

import com.main.tancol.orden.entities.OrdenEntity;

import java.util.Optional;
import java.util.UUID;

public interface CheckDeleted<T> {

    /**
     * Busca la ID que exista y no este en verdadero
     * @param id
     * @return
     */
    Optional<T> findByIdAndDeletedFalse(UUID id);
}
