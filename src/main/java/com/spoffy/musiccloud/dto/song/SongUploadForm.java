package com.spoffy.musiccloud.dto.song;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record SongUploadForm(
	@Schema(example = "Midnight Drive", description = "Título de la canción.")
	@NotBlank String title,
	@Schema(example = "Soffy Wave", description = "Artista principal.")
	@NotBlank String artist,
	@Schema(example = "lofi, chill, instrumental", description = "Etiquetas o metadata separadas por coma.")
	String tags) {
}