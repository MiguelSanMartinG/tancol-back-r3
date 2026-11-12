package com.main.tancol.auth.controller;

import com.main.tancol.auth.dtos.CreateUserDto;
import com.main.tancol.auth.dtos.RegisterUserDto;
import com.main.tancol.auth.entities.User;
import com.main.tancol.auth.services.AuthenticationService;
import com.main.tancol.auth.services.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/user")
@Hidden
@RestController
@Tag(name = "Usuario")
public class UsersController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    public UsersController(AuthenticationService authenticationService, UserService userService) {
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<User>> getUser() {
        return ResponseEntity.ok(authenticationService.getAllUsers());
    }

    @PostMapping("/admin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<User> createAdministrator(@RequestBody RegisterUserDto registerUserDto) {
        User createdAdmin = userService.createAdministrator(registerUserDto);
        return ResponseEntity.ok(createdAdmin);
    }

    @PostMapping("/create-user")
//    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<User> createUser(@RequestBody CreateUserDto createUser) {
        User createdUser = userService.createUser(createUser);
        return ResponseEntity.ok(createdUser);
    }

    @DeleteMapping("/delete-user/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<String> deleteUser(@PathVariable UUID userId) {
        User user = userService.toggleUser(userId);
        if (user != null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("Usuario esta "+user.isEnabled());
    }

    @GetMapping("/user-list")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'SUPER_ADMIN')")
    public ResponseEntity<List<User>> getUserList() {
        return ResponseEntity.ok(authenticationService.getAllUsers());
    }


}
