package com.main.tancol.auth.configs;

//import com.main.tancol.auth.repositories.UserRepository;

import com.main.tancol.empleados.repositories.EmpleadosRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// Sobrescribe la configuration de Spring.
@Configuration
public class ApplicationConfiguration {

    private final EmpleadosRepository empleadosRepository;

    public ApplicationConfiguration(EmpleadosRepository userRepository) {
        this.empleadosRepository = userRepository;
    }

    // Define como recuperar el usuario.
    @Bean
    UserDetailsService userDetailsService() {
        return username -> empleadosRepository.findByNombreUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // Crea una instancia de encoder.
    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Establece una nueva estrategia para hacer la authentication.
    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }



}
