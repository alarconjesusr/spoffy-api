package com.spoffy.musiccloud.service;

import com.spoffy.musiccloud.domain.Album;
import com.spoffy.musiccloud.domain.AlbumType;
import com.spoffy.musiccloud.domain.Artist;
import com.spoffy.musiccloud.dto.album.AlbumResponseDto;
import com.spoffy.musiccloud.repository.AlbumRepository;


import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlbumService {    

    private final AlbumRepository albumRepository;
    private final StorageService storageService;

    public List<AlbumResponseDto> getNewAlbums() {
        return albumRepository.findByOrderByCreatedAtDesc()
                .stream()
                .map(this::toAlbumResponseDto)
                .toList();
    }

    @Transactional
    public Album resolveOrCreateAlbum(String albumTitle, Integer releaseYear, String coverStorageKey, Artist artist) {
        if (albumTitle == null || albumTitle.isBlank() || artist == null || artist.getId() == null) {
            return null;
        }

        String normalizedTitle = albumTitle.trim();
        return albumRepository.findByPrimaryArtistIdAndTitleIgnoreCase(artist.getId(), normalizedTitle)
                .map(existing -> updateAlbumMetadata(existing, releaseYear, coverStorageKey))
                .orElseGet(() -> {
                    Album album = new Album();
                    album.setTitle(normalizedTitle);
                    album.setSlug(buildSlug(normalizedTitle));
                    album.setPrimaryArtist(artist);
                    album.setAlbumType(AlbumType.SINGLE);
                    album.setCoverStorageKey(coverStorageKey);
                    if (releaseYear != null) {
                        album.setReleaseYear(releaseYear);
                        album.setReleaseDate(LocalDate.of(releaseYear, 1, 1));
                    }
                    return albumRepository.save(album);
                });
    }

    private Album updateAlbumMetadata(Album album, Integer releaseYear, String coverStorageKey) {
        boolean changed = false;

        if (releaseYear != null && album.getReleaseYear() == null) {
            album.setReleaseYear(releaseYear);
            if (album.getReleaseDate() == null) {
                album.setReleaseDate(LocalDate.of(releaseYear, 1, 1));
            }
            changed = true;
        }

        if (coverStorageKey != null && !coverStorageKey.isBlank()
                && (album.getCoverStorageKey() == null || album.getCoverStorageKey().isBlank())) {
            album.setCoverStorageKey(coverStorageKey);
            changed = true;
        }

        return changed ? albumRepository.save(album) : album;
    }

    private String buildSlug(String rawValue) {
        String slug = rawValue.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-+|-+$)", "");
        return slug.isBlank() ? "album" : slug;
    }

    private AlbumResponseDto toAlbumResponseDto(Album album) {
        String albumCoverStorageKey = album == null ? null : album.getCoverStorageKey();
        String albumCoverUrl = albumCoverStorageKey == null || albumCoverStorageKey.isBlank()
                ? null
                : storageService.getPresignedGetUrl(albumCoverStorageKey);

        return new AlbumResponseDto(
                album.getId(),
                album.getTitle(),
                album.getSlug(),
                album.getAlbumType(),
                album.getReleaseDate(),
                album.getReleaseYear(),
                album.getLabel(),
                albumCoverUrl
            );
    }
}