package com.spoffy.musiccloud.repository;

import com.spoffy.musiccloud.domain.Album;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumRepository extends JpaRepository<Album, UUID> {
	Optional<Album> findByPrimaryArtistIdAndTitleIgnoreCase(UUID artistId, String title);
	List<Album> findByOrderByCreatedAtDesc();
}