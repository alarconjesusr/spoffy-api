package com.spoffy.musiccloud.repository;

import com.spoffy.musiccloud.domain.SongAudioMetadata;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongAudioMetadataRepository extends JpaRepository<SongAudioMetadata, UUID> {
	Optional<SongAudioMetadata> findBySongId(UUID songId);
}