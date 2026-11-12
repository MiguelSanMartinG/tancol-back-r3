package com.main.tancol.auth.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * DTO for {@link com.main.tancol.auth.entities.User}
 */
@Value
@Slf4j
public class CreateUserDto implements Serializable {
    @NotBlank
    String fullName;
    @NotNull
    @NotEmpty
    @NotBlank
    String email;
    @NotNull
    @NotEmpty
    @NotBlank
    String password;
}