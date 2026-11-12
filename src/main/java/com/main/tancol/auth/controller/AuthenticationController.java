package com.main.tancol.auth.controller;

import com.main.tancol.auth.dtos.LoginRequest;
import com.main.tancol.auth.dtos.LoginResponse;
import com.main.tancol.auth.dtos.RegisterUserDto;
import com.main.tancol.auth.services.AuthenticationService;
import com.main.tancol.auth.services.JwtService;
import com.main.tancol.empleados.entities.EmpleadosEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
@Tag(name = "🔐 Authentication")
@RequiredArgsConstructor
public class AuthenticationController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

//    @PostMapping("/signup")
//    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterUserDto registerUserDto) {
//        EmpleadosEntity registeredUser = authenticationService.signup(registerUserDto);
//        return ResponseEntity.ok(registeredUser);
//    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginRequest loginUserDto) {
        EmpleadosEntity authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = LoginResponse.builder()
                .Nombre(authenticatedUser.getNombre())
                .ApellidoPaterno(authenticatedUser.getPrimerApellido())
                .idEmpleado(authenticatedUser.getId())
                .token(jwtToken)
                .expiresIn(jwtService.getExpirationTime())
                .build();

        return ResponseEntity.ok(loginResponse);
    }


}
