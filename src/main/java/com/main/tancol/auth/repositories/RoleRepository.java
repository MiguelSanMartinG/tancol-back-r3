package com.main.tancol.auth.repositories;

import com.main.tancol.auth.entities.Role;
import com.main.tancol.auth.entities.enums.RoleEnum;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends CrudRepository<Role, UUID> {
    Optional<Role> findByName(RoleEnum name);
}
