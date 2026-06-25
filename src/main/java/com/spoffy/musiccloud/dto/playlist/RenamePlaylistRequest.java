package com.spoffy.musiccloud.dto.playlist;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record RenamePlaylistRequest(
	@Schema(example = "Mi playlist favorita", description = "Nuevo nombre para la playlist.")
	@NotBlank String name) {
}