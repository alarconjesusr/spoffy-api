package com.spoffy.musiccloud.dto.playlist;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PlaylistResponse(
	@Schema(example = "33333333-3333-3333-3333-333333333333") UUID id,
	@Schema(example = "Mi playlist chill") String name,
	@Schema(example = "11111111-1111-1111-1111-111111111111") UUID userId,
	Instant createdAt,
	List<PlaylistSongResponse> songs) {
}