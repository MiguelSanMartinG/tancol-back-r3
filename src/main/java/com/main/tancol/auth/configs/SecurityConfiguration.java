package com.main.tancol.auth.configs;


import com.main.tancol.auth.exceptions.customHandlers.CustomAccessDeniedHandler;
import com.main.tancol.auth.exceptions.customHandlers.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter, AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    //Se encarga de interceptar y manejar las solicitudes HTTP antes de llegar a los controladores.
    @Profile("???")
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                // Cross-Site Request Forgery (Falsificación de solicitud en sitios cruzados).
                .cors(
                        cors -> cors.configurationSource(
                                request -> {
                            CorsConfiguration configuration = new CorsConfiguration();
                            configuration.setAllowedOrigins(List.of("*"));
                            configuration.setAllowedMethods(List.of("*"));
                            configuration.setAllowedHeaders(List.of("*"));
                            return configuration;
                        }
                ))
                // Es una vulnerabilidad que permite que un atacante realice solicitudes malintencionadas en nombre de un usuario autenticado.
                .authorizeHttpRequests(
                        request -> request
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/user/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/api-docs/**").permitAll()
                        .requestMatchers("/departamento/**").permitAll()
                        .anyRequest().authenticated() //Indica que todas las demás solicitudes que no estén definidas previamente requerirán autenticación.
                )
                .authenticationProvider(authenticationProvider)
                // Es un componente que válida las credenciales del usuario.
                // Define cómo se autentican los usuarios, verificando usuario, contraseña y roles.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                                .accessDeniedHandler(new CustomAccessDeniedHandler())
                )
                .build();

    }

    @Profile({"dev", "test"})
    @Bean
    public SecurityFilterChain filterChain2(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                // Cross-Site Request Forgery (Falsificación de solicitud en sitios cruzados).
                .cors(
                        cors -> cors.configurationSource(
                                request -> {
                                    CorsConfiguration configuration = new CorsConfiguration();
                                    configuration.setAllowedOrigins(List.of("*"));
                                    configuration.setAllowedMethods(List.of("*"));
                                    configuration.setAllowedHeaders(List.of("*"));
                                    return configuration;
                                }
                        ))
                // Es una vulnerabilidad que permite que un atacante realice solicitudes malintencionadas en nombre de un usuario autenticado.
                .authorizeHttpRequests(
                        request -> request
                                .anyRequest().permitAll() //Indica que todas las demás solicitudes que no estén definidas previamente requerirán autenticación.
                )

                // Es un componente que válida las credenciales del usuario.
                // Define cómo se autentican los usuarios, verificando usuario, contraseña y roles.
                .build();

    }



}