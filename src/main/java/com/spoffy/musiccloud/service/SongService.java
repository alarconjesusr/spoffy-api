package com.spoffy.musiccloud.service;

import com.spoffy.musiccloud.domain.Album;
import com.spoffy.musiccloud.domain.Artist;
import com.spoffy.musiccloud.domain.Song;
import com.spoffy.musiccloud.domain.SongArtist;
import com.spoffy.musiccloud.domain.SongArtistRole;
import com.spoffy.musiccloud.domain.SongAudioMetadata;
import com.spoffy.musiccloud.domain.User;
import com.spoffy.musiccloud.dto.song.SongResponse;
import com.spoffy.musiccloud.dto.song.SongSearchResponse;
import com.spoffy.musiccloud.dto.song.SongTopResponse;
import com.spoffy.musiccloud.dto.song.SongUploadForm;
import com.spoffy.musiccloud.exception.ResourceNotFoundException;
import com.spoffy.musiccloud.repository.PlaylistSongRepository;
import com.spoffy.musiccloud.repository.SongArtistRepository;
import com.spoffy.musiccloud.repository.SongRepository;
import com.spoffy.musiccloud.service.SongMetadataService.ExtractedSongMetadata;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class SongService {

    private static final int COVER_URL_EXPIRY_SECONDS = 60 * 60;

    private final SongRepository songRepository;
    private final ArtistService artistService;
    private final AlbumService albumService;
    private final SongMetadataService songMetadataService;
    private final SongArtistRepository songArtistRepository;
    private final PlaylistSongRepository playlistSongRepository;
    private final StorageService storageService;

    @Transactional
    public SongResponse upload(MultipartFile file, SongUploadForm form, User uploader) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }

        ExtractedSongMetadata extractedMetadata = songMetadataService.extractFromFile(file);

        String effectiveTitle = firstNonBlank(extractedMetadata.title(), form.title());
        String effectiveArtistName = firstNonBlank(extractedMetadata.artist(), form.artist());
        String effectiveTags = firstNonBlank(extractedMetadata.genre(), form.tags());

        String storageKey = "songs/" + UUID.randomUUID();
        String albumCoverStorageKey = null;

        try {
            storageService.upload(storageKey, file.getInputStream(), file.getSize(),
                    file.getContentType() == null ? "application/octet-stream" : file.getContentType());
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to read uploaded file", ex);
        }

        if (extractedMetadata.hasPictureData()) {
            albumCoverStorageKey = "album-cover/" + UUID.randomUUID();
            String pictureMimeType = extractedMetadata.pictureMimeType() == null
                    || extractedMetadata.pictureMimeType().isBlank()
                            ? "image/jpeg"
                            : extractedMetadata.pictureMimeType();
            storageService.upload(
                    albumCoverStorageKey,
                    new ByteArrayInputStream(extractedMetadata.pictureData()),
                    extractedMetadata.pictureData().length,
                    pictureMimeType);
        }

        Artist primaryArtist = artistService.resolveOrCreateArtist(effectiveArtistName);
        Album resolvedAlbum = albumService.resolveOrCreateAlbum(
                extractedMetadata.album(),
                extractedMetadata.year(),
                albumCoverStorageKey,
                primaryArtist);

        Song song = new Song();
        song.setTitle(effectiveTitle);
        song.setArtist(primaryArtist.getName());
        song.setTags(effectiveTags);
        song.setAlbum(resolvedAlbum);
        song.setStorageKey(storageKey);
        song.setOriginalFilename(file.getOriginalFilename() == null ? "audio" : file.getOriginalFilename());
        song.setContentType(file.getContentType() == null ? "application/octet-stream" : file.getContentType());
        song.setSizeBytes(file.getSize());
        song.setTrackNumber(extractedMetadata.trackNumber());
        song.setDiscNumber(extractedMetadata.discNumber());
        song.setDurationSec(extractedMetadata.durationSec());
        song.setLanguageCode(extractedMetadata.languageCode());
        song.setIsrc(extractedMetadata.isrc());
        song.setLyrics(extractedMetadata.lyrics());
        song.setUploadedBy(uploader);
        Song saved = songRepository.save(song);

        SongArtist songArtist = new SongArtist();
        songArtist.setSong(saved);
        songArtist.setArtist(primaryArtist);
        songArtist.setRole(SongArtistRole.PRIMARY);
        songArtist.setSortOrder(0);
        songArtistRepository.save(songArtist);

        songMetadataService.saveMetadata(saved, extractedMetadata);

        return toResponse(saved);
    }

    public Song getRequiredSong(UUID id) {
        return songRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Song not found"));
    }

    public SongResponse getSong(UUID id) {
        return toResponse(getRequiredSong(id));
    }

    @Transactional
    public void registerPlay(UUID id) {
        int updatedRows = songRepository.incrementPlayCount(id);
        if (updatedRows == 0) {
            throw new ResourceNotFoundException("Song not found");
        }
    }

    public List<SongTopResponse> getTopSongs() {
        return songRepository.findTop10ByOrderByPlayCountDescCreatedAtDesc()
                .stream()
                .map(this::toTopResponse)
                .toList();
    }

    public List<SongSearchResponse> search(String query, String artist, String tag) {
        if (query == null && artist == null && tag == null) {
            return songRepository.findAll().stream().map(this::toSearchResponse).toList();
        }
        String normalizedQuery = query == null ? "" : query;
        String normalizedArtist = artist == null ? normalizedQuery : artist;
        String normalizedTag = tag == null ? normalizedQuery : tag;
        return songRepository.findByTitleContainingIgnoreCaseOrArtistContainingIgnoreCaseOrTagsContainingIgnoreCase(
                normalizedQuery, normalizedArtist, normalizedTag)
                .stream()
                .map(this::toSearchResponse)
                .toList();
    }

    @Transactional
    public void deleteSong(UUID id) {
        Song song = getRequiredSong(id);
        playlistSongRepository.deleteBySongId(id);
        storageService.delete(song.getStorageKey());
        songRepository.delete(song);
    }

    private SongResponse toResponse(Song song) {
        return new SongResponse(song.getId(), song.getTitle(), song.getArtist(), song.getTags(), song.getContentType(),
                song.getSizeBytes(), song.getPlayCount(), song.getOriginalFilename(),
                song.getUploadedBy() == null ? null : song.getUploadedBy().getId(), song.getCreatedAt());
    }

    private SongSearchResponse toSearchResponse(Song song) {
        return new SongSearchResponse(song.getId(), song.getTitle(), song.getArtist(), song.getTags());
    }

    private SongTopResponse toTopResponse(Song song) {
        Album album = song.getAlbum();
        SongAudioMetadata metadata = song.getAudioMetadata();
        String albumCoverStorageKey = album == null ? null : album.getCoverStorageKey();
        String albumCoverUrl = albumCoverStorageKey == null || albumCoverStorageKey.isBlank()
                ? null
                : storageService.getPresignedGetUrl(albumCoverStorageKey, COVER_URL_EXPIRY_SECONDS);

        return new SongTopResponse(
                song.getId(),
                song.getTitle(),
                song.getArtist(),
                song.getPlayCount(),
                album == null ? null : album.getId(),
                album == null ? null : album.getTitle(),
                albumCoverStorageKey,
                albumCoverUrl,
                metadata == null ? null : metadata.getDetectedAlbum(),
                metadata == null ? null : metadata.getDetectedYear(),
                metadata == null ? null : metadata.getDurationSec());
    }

    private String firstNonBlank(String first, String fallback) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        if (fallback != null && !fallback.isBlank()) {
            return fallback;
        }
        return null;
    }
}