package com.main.tancol.preferencias;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PreferenciaUsuarioRepository extends JpaRepository<PreferenciaUsuarioEntity, UUID> {

    List<PreferenciaUsuarioEntity> findByEmpleado_IdAndDeletedFalse(UUID empleadoId);

    Optional<PreferenciaUsuarioEntity> findByEmpleado_IdAndClave(UUID empleadoId, String clave);
}
