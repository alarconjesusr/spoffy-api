package com.spoffy.musiccloud.dto.song;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

public record SongTopResponse(
        @Schema(example = "22222222-2222-2222-2222-222222222222") UUID id,
        @Schema(example = "A Certain Romance") String title,
        @Schema(example = "Arctic Monkeys") String artist,
        @Schema(example = "4512") long playCount,
        @Schema(example = "33333333-3333-3333-3333-333333333333") UUID albumId,
        @Schema(example = "Whatever People Say I Am, That's What I'm Not") String albumTitle,
        @Schema(example = "album-cover/0fca445d-e301-4e18-8e6c-8fe9b4183efb") String albumCoverStorageKey,
        @Schema(example = "http://localhost:9000/music/album-cover/0fca445d-e301-4e18-8e6c-8fe9b4183efb?X-Amz-Algorithm=AWS4-HMAC-SHA256...") String albumCoverUrl,
        @Schema(example = "Whatever People Say I Am, That's What I'm Not") String metadataAlbum,
        @Schema(example = "2014") Integer metadataYear,
        @Schema(example = "215") Integer metadataDurationSec) {
}