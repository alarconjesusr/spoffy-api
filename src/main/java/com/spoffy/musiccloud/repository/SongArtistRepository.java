package com.spoffy.musiccloud.repository;

import com.spoffy.musiccloud.domain.SongArtist;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongArtistRepository extends JpaRepository<SongArtist, UUID> {
}