package com.main.tancol.auth.bootstrap;

import com.main.tancol.auth.dtos.RegisterUserDto;
import com.main.tancol.auth.entities.Role;
import com.main.tancol.auth.entities.User;
import com.main.tancol.auth.entities.enums.RoleEnum;
import com.main.tancol.auth.repositories.RoleRepository;
import com.main.tancol.auth.repositories.UserRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


import java.util.Optional;

@Component
@Order(2)
public class AdminSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    public AdminSeeder(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.createSuperAdministrator();
    }

    private void createUserDefault(){
        Optional<User> userDB = userRepository.findByEmail("super.admin@email.com");

        if(userDB.isPresent()){
            System.out.println("Ya existe el user");
            return;
        }

        User user = new User();
        user.setEmail("super.admin@email.com").setPassword(passwordEncoder.encode("123456")).setFullName("Super Admin").setEnabled(true);
        userRepository.save(user);
        createSuperAdministrator();

    }

    private void createSuperAdministrator() {

        RegisterUserDto userDto = new RegisterUserDto();
        userDto.setFirstName("Super Admin").setUsername("super.admin@email.com").setPassword("123456");

        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.SUPER_ADMIN);
        Optional<User> optionalUser = userRepository.findByEmail(userDto.getUsername());

        if (optionalRole.isEmpty() || optionalUser.isPresent()) {
            System.out.println("ERROR - No se encontró un usuario o rol. ");
            return;
        }

        var user = new User()
                .setFullName(userDto.getFirstName())
                .setEmail(userDto.getUsername())
                .setPassword(passwordEncoder.encode(userDto.getPassword()))
                .setRole(optionalRole.get());

        userRepository.save(user);
    }
}
