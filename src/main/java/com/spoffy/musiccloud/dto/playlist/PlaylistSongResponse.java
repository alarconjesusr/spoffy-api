package com.spoffy.musiccloud.dto.playlist;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

public record PlaylistSongResponse(
	@Schema(example = "22222222-2222-2222-2222-222222222222") UUID songId,
	@Schema(example = "Midnight Drive") String title,
	@Schema(example = "Soffy Wave") String artist) {
}