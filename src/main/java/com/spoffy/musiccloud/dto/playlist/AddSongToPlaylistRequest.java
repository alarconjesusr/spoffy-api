package com.spoffy.musiccloud.dto.playlist;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AddSongToPlaylistRequest(
	@Schema(example = "22222222-2222-2222-2222-222222222222", description = "UUID de una canción ya subida.")
	@NotNull UUID songId) {
}