package com.spoffy.musiccloud.repository;

import com.spoffy.musiccloud.domain.Song;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRepository extends JpaRepository<Song, UUID> {
    List<Song> findByTitleContainingIgnoreCaseOrArtistContainingIgnoreCaseOrTagsContainingIgnoreCase(String title, String artist, String tags);
}