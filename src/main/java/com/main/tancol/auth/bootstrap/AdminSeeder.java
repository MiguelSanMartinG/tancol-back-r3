package com.main.tancol.auth.bootstrap;

//import com.main.tancol.auth.entities.User;
//import com.main.tancol.auth.repositories.UserRepository;

import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.empleados.repositories.EmpleadosRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AdminSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final PasswordEncoder passwordEncoder;
    private final EmpleadosRepository empleadosRepository;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.createSuperAdministrator();

    }

    private void createSuperAdministrator() {
        Boolean existeAdmin = this.empleadosRepository.existsByIdEmpleado(0L);

        if(existeAdmin) return;
        EmpleadosEntity entity =  EmpleadosEntity.builder()
                .nombreUsuario("admin")
                .idEmpleado(0L)
                .build();
        entity.setContra(passwordEncoder.encode("123"));
        this.empleadosRepository.save(entity);
    }

}
