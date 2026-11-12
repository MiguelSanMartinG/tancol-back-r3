package com.main.tancol.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginRequest {
    @Schema(description = "Nombre de usuario", example = "admin")
    private String nombreUsuario;
    @Schema(description = "Contraseña", example = "string")
    private String contrasena;
}
