package com.spoffy.musiccloud.dto.album;

import java.time.LocalDate;
import java.util.UUID;

import com.spoffy.musiccloud.domain.AlbumType;

public record AlbumResponseDto (
    UUID id,    
    String title,
    String slug,    
    AlbumType albumType,    
    LocalDate releaseDate,    
    Integer releaseYear,    
    String label,
    String albumCoverUrl
) {}
