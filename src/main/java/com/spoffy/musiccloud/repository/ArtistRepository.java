package com.spoffy.musiccloud.repository;

import com.spoffy.musiccloud.domain.Artist;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistRepository extends JpaRepository<Artist, UUID> {
    Optional<Artist> findByNameIgnoreCase(String name);

    Optional<Artist> findBySlug(String slug);

    boolean existsBySlug(String slug);
}