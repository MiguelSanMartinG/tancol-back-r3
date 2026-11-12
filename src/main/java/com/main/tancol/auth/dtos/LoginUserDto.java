package com.main.tancol.auth.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

public class LoginUserDto {
    @Schema(description = "Nombre de usuario", example = "super.admin@email.com")
    private String email;
    @Schema(description = "Contrseña", example = "123456")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
