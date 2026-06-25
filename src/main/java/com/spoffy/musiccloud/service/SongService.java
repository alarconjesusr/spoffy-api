package com.spoffy.musiccloud.service;

import com.spoffy.musiccloud.domain.Song;
import com.spoffy.musiccloud.domain.User;
import com.spoffy.musiccloud.dto.song.SongResponse;
import com.spoffy.musiccloud.dto.song.SongSearchResponse;
import com.spoffy.musiccloud.dto.song.SongUploadForm;
import com.spoffy.musiccloud.exception.ResourceNotFoundException;
import com.spoffy.musiccloud.repository.PlaylistSongRepository;
import com.spoffy.musiccloud.repository.SongRepository;
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

    private final SongRepository songRepository;
    private final PlaylistSongRepository playlistSongRepository;
    private final StorageService storageService;

    @Transactional
    public SongResponse upload(MultipartFile file, SongUploadForm form, User uploader) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }
        String storageKey = "songs/" + UUID.randomUUID();
        try {
            storageService.upload(storageKey, file.getInputStream(), file.getSize(), file.getContentType() == null ? "application/octet-stream" : file.getContentType());
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to read uploaded file", ex);
        }

        Song song = new Song();
        song.setTitle(form.title());
        song.setArtist(form.artist());
        song.setTags(form.tags());
        song.setStorageKey(storageKey);
        song.setOriginalFilename(file.getOriginalFilename() == null ? "audio" : file.getOriginalFilename());
        song.setContentType(file.getContentType() == null ? "application/octet-stream" : file.getContentType());
        song.setSizeBytes(file.getSize());
        song.setUploadedBy(uploader);
        Song saved = songRepository.save(song);
        return toResponse(saved);
    }

    public Song getRequiredSong(UUID id) {
        return songRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Song not found"));
    }

    public SongResponse getSong(UUID id) {
        return toResponse(getRequiredSong(id));
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
        return new SongResponse(song.getId(), song.getTitle(), song.getArtist(), song.getTags(), song.getContentType(), song.getSizeBytes(), song.getOriginalFilename(), song.getUploadedBy() == null ? null : song.getUploadedBy().getId(), song.getCreatedAt());
    }

    private SongSearchResponse toSearchResponse(Song song) {
        return new SongSearchResponse(song.getId(), song.getTitle(), song.getArtist(), song.getTags());
    }
}