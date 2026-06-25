package com.spoffy.musiccloud.dto.song;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

public record SongSearchResponse(
	@Schema(example = "22222222-2222-2222-2222-222222222222") UUID id,
	@Schema(example = "Midnight Drive") String title,
	@Schema(example = "Soffy Wave") String artist,
	@Schema(example = "lofi, chill, instrumental") String tags) {
}