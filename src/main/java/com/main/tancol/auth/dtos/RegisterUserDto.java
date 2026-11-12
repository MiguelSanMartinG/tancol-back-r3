package com.main.tancol.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterUserDto {

    @Schema(description = "Nombre de usuario", example = "super.admin@email.com")
    @NotNull(message = "El correo no puede ser nulo")
    @Email(message = "El formato del correo electrónico es incorrecto")
    private String username;
    @NotEmpty
    @NotNull
    @Schema(description = "Contrseña", example = "123456")
//    @Pattern(
//            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
//            message = "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial"
//    )
    private String password;
//    @NotEmpty
//    private String firstName;


//    public RegisterUserDto setFirstName(String firstName) {
//        this.firstName = firstName;
//        return this;
//    }
//
//    public RegisterUserDto setPassword(String password) {
//        this.password = password;
//        return this;
//    }
//
//    public RegisterUserDto setUsername(String username) {
//        this.username = username;
//        return this;
//    }
}
