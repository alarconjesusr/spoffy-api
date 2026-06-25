package com.spoffy.musiccloud.dto.playlist;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CreatePlaylistRequest(
	@Schema(example = "Mi playlist chill", description = "Nombre visible de la playlist.")
	@NotBlank String name) {
}