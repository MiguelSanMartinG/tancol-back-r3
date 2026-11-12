package com.main.tancol.auth.services;

import com.main.tancol.auth.dtos.LoginRequest;
import com.main.tancol.auth.dtos.RegisterUserDto;
import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import com.main.tancol.empleados.repositories.EmpleadosRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
//    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmpleadosRepository empleadosRepository;



//    public User signup(RegisterUserDto input) {
//
//        if(userRepository.existsByEmail(input.getUsername())){
//            throw new EmailAlreadyExist("El email ya se encuentra registrado.");
//        }
//
//        //Se busca el rol usuario y se asigna.
////        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.USER);
////
////        if (optionalRole.isEmpty()){
////            return null;
////        }
//
//        User user = new User()
//                .setFullName(input.getFirstName())
//                .setEmail(input.getUsername())
////                .setRole(optionalRole.get())
//                .setPassword(passwordEncoder.encode(input.getPassword()));
//
//
//        return userRepository.save(user);
//    }

//    Handles user authentication. It delegates authentication to AuthenticationProviders.
    public EmpleadosEntity authenticate(LoginRequest request) {

//        log.info("Intentando autenticar al usuario: {}", request.getNombreUsuario());

        EmpleadosEntity empleadosEntity = this.empleadosRepository.findByNombreUsuario(request.getNombreUsuario()).orElseThrow(() -> new IDNotFound("No se encontro el usuario"));

//        log.info("Autenticando el usuario: {}", empleadosEntity.getPassword());
        if (!passwordEncoder.matches(request.getContrasena(), empleadosEntity.getPassword())) {
            throw new BadCredentialsException("Bad credentials");
        }


//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        request.getNombreUsuario(),
//                        request.getContrasena()
//                )
//        );


        log.info("Autenticación exitosa");
        return empleadosEntity;
    }




//    public List<User> getAllUsers() {
//        return (List<User>) userRepository.findAll();
//    }

}
