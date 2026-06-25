package com.spoffy.musiccloud.repository;

import com.spoffy.musiccloud.domain.PlaylistSong;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistSongRepository extends JpaRepository<PlaylistSong, UUID> {
    Optional<PlaylistSong> findByPlaylistIdAndSongId(UUID playlistId, UUID songId);

    void deleteByPlaylistIdAndSongId(UUID playlistId, UUID songId);

    void deleteBySongId(UUID songId);
}