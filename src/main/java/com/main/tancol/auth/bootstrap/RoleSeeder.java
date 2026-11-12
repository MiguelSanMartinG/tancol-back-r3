package com.main.tancol.auth.bootstrap;

import com.main.tancol.auth.entities.enums.RoleEnum;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Map;

// Clase que se ejecuta tras iniciar el proyecto;
//@Component
//@Order(1)
public class RoleSeeder implements ApplicationListener<ContextRefreshedEvent> {

//    // Inyeccion
//    private final RoleRepository roleRepository;
//
//
//    public RoleSeeder(RoleRepository roleRepository) {
//        this.roleRepository = roleRepository;
//
//    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.loadRoles();
    }

    private void loadRoles() {
        //Se crea un array de roles
        RoleEnum[] roleNames = new RoleEnum[]{RoleEnum.USER, RoleEnum.ADMIN, RoleEnum.SUPER_ADMIN};
        //Se hace un map
        Map<RoleEnum, String> roleDescriptionMap = Map.of(
                RoleEnum.USER, "Default user role",
                RoleEnum.ADMIN, "Administrator role",
                RoleEnum.SUPER_ADMIN, "Super Administrator role"
        );

//        Arrays.stream(roleNames).forEach((roleName) -> {
//            //Buscamos los nombres de los roles
//            Optional<Role> optionalRole = roleRepository.findByName(roleName);
//
//            // Si hay roles con esos nombres
//            optionalRole.ifPresentOrElse(System.out::println, () -> {
//                Role roleToCreate = new Role();
//
//                roleToCreate.setName(roleName)
//                        .setDescription(roleDescriptionMap.get(roleName));
//
//                roleRepository.save(roleToCreate);
//            });
//        });
    }


}
