package com.spoffy.musiccloud.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
	@Schema(example = "usuario.demo@spoffy.dev", description = "Correo registrado.")
	@Email @NotBlank String email,
	@Schema(example = "DemoPass123!", description = "Contraseña del usuario.")
	@NotBlank String password) {
}