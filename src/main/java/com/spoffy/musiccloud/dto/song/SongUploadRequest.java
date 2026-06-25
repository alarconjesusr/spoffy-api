package com.spoffy.musiccloud.dto.song;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record SongUploadRequest(
        @Schema(example = "Midnight Drive", description = "Título de la canción.")
        @NotBlank String title,
        @Schema(example = "Soffy Wave", description = "Artista principal.")
        @NotBlank String artist,
        @Schema(example = "lofi, chill, instrumental", description = "Etiquetas o metadata separadas por coma.")
        String tags,
        @Schema(type = "string", format = "binary", description = "Archivo de audio a subir.")
        @NotNull MultipartFile file) {
}