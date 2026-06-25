package com.spoffy.musiccloud.dto.song;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

public record SongResponse(
	@Schema(example = "22222222-2222-2222-2222-222222222222") UUID id,
	@Schema(example = "Midnight Drive") String title,
	@Schema(example = "Soffy Wave") String artist,
	@Schema(example = "lofi, chill, instrumental") String tags,
	@Schema(example = "audio/mpeg") String contentType,
	@Schema(example = "7340032") long sizeBytes,
	@Schema(example = "midnight-drive.mp3") String originalFilename,
	@Schema(example = "11111111-1111-1111-1111-111111111111") UUID uploadedBy,
	Instant createdAt) {
}