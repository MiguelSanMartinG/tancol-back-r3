package com.main.tancol.auth.services;

import com.main.tancol.auth.dtos.LoginUserDto;
import com.main.tancol.auth.dtos.RegisterUserDto;
import com.main.tancol.auth.entities.Role;
import com.main.tancol.auth.entities.User;
import com.main.tancol.auth.entities.enums.RoleEnum;
import com.main.tancol.auth.exceptions.exeption.EmailAlreadyExist;
import com.main.tancol.auth.repositories.RoleRepository;
import com.main.tancol.auth.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            RoleRepository roleRepository)
    {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public User signup(RegisterUserDto input) {

        if(userRepository.existsByEmail(input.getUsername())){
            throw new EmailAlreadyExist("El email ya se encuentra registrado.");
        }

        //Se busca el rol usuario y se asigna.
        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.USER);

        if (optionalRole.isEmpty()){
            return null;
        }

        User user = new User()
                .setFullName(input.getFirstName())
                .setEmail(input.getUsername())
                .setRole(optionalRole.get())
                .setPassword(passwordEncoder.encode(input.getPassword()));


        return userRepository.save(user);
    }

//    Handles user authentication. It delegates authentication to AuthenticationProviders.
    public User authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return userRepository.findByEmail(input.getEmail())
                .orElseThrow();
    }


    public List<User> getAllUsers() {
        return (List<User>) userRepository.findAll();
    }

}
