package com.main.tancol.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class LoginResponse {
    private String token;
    private String Nombre;
    private String ApellidoPaterno;
    private UUID idEmpleado;
    /** Foto de perfil del empleado (nombre en /images) o null. */
    private String idImagen;
    private long expiresIn;


}
