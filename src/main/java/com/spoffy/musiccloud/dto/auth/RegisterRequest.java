package com.spoffy.musiccloud.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

public record RegisterRequest(
        @Schema(example = "usuario.demo@spoffy.dev", description = "Correo único para iniciar sesión.")
        @Email @NotBlank String email,
        @Schema(example = "Usuario Demo", description = "Nombre visible del usuario.")
        @NotBlank String displayName,
        @Schema(example = "DemoPass123!", description = "Contraseña segura de al menos 8 caracteres.")
        @NotBlank @Size(min = 8, max = 72) String password) {
}