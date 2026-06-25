package com.spoffy.musiccloud.controller;

import com.spoffy.musiccloud.dto.auth.AuthResponse;
import com.spoffy.musiccloud.dto.auth.LoginRequest;
import com.spoffy.musiccloud.dto.auth.RegisterRequest;
import com.spoffy.musiccloud.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Registro e inicio de sesión con JWT.")
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Registrar usuario", description = "Crea un usuario nuevo y devuelve un JWT listo para usar.")
    @ApiResponse(responseCode = "201", description = "Usuario creado", content = @Content(schema = @Schema(implementation = AuthResponse.class)))
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica con email y contraseña y devuelve un JWT.")
    @ApiResponse(responseCode = "200", description = "Login correcto", content = @Content(schema = @Schema(implementation = AuthResponse.class)))
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request);
    }
}