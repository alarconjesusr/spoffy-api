package com.spoffy.musiccloud.repository;

import com.spoffy.musiccloud.domain.Song;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SongRepository extends JpaRepository<Song, UUID> {
    List<Song> findByTitleContainingIgnoreCaseOrArtistContainingIgnoreCaseOrTagsContainingIgnoreCase(String title, String artist, String tags);

    @EntityGraph(attributePaths = {"album", "audioMetadata"})
    List<Song> findTop10ByOrderByPlayCountDescCreatedAtDesc();

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Song s set s.playCount = s.playCount + 1 where s.id = :id")
    int incrementPlayCount(@Param("id") UUID id);
}