package com.spoffy.musiccloud.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

public record AuthResponse(
	@Schema(example = "11111111-1111-1111-1111-111111111111") UUID userId,
	@Schema(example = "usuario.demo@spoffy.dev") String email,
	@Schema(example = "Usuario Demo") String displayName,
	@Schema(example = "USER") String role,
	@Schema(example = "eyJhbGciOiJIUzI1NiJ9...", description = "JWT para usar en el header Authorization.") String token) {
}