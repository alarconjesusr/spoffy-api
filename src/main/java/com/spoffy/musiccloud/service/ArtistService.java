package com.spoffy.musiccloud.service;

import com.spoffy.musiccloud.domain.Artist;
import com.spoffy.musiccloud.repository.ArtistRepository;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistRepository artistRepository;

    @Transactional
    public Artist resolveOrCreateArtist(String artistName) {
        String normalizedName = artistName == null ? "Unknown Artist" : artistName.trim();
        if (normalizedName.isEmpty()) {
            normalizedName = "Unknown Artist";
        }
        final String finalName = normalizedName;

        return artistRepository.findByNameIgnoreCase(finalName)
                .orElseGet(() -> {
                    Artist artist = new Artist();
                    artist.setName(finalName);
                    artist.setSlug(buildUniqueSlug(finalName));
                    return artistRepository.save(artist);
                });
    }

    private String buildUniqueSlug(String rawName) {
        String baseSlug = rawName.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-+|-+$)", "");
        if (baseSlug.isBlank()) {
            baseSlug = "artist";
        }

        String candidate = baseSlug;
        int suffix = 2;
        while (artistRepository.existsBySlug(candidate)) {
            candidate = baseSlug + "-" + suffix;
            suffix++;
        }
        return candidate;
    }
}