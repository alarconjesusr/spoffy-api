package com.spoffy.musiccloud.service;

import com.spoffy.musiccloud.domain.Song;
import com.spoffy.musiccloud.exception.InvalidRangeException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StreamingService {

    private static final Pattern RANGE_PATTERN = Pattern.compile("bytes=(\\d+)-(\\d*)");

    private final StorageService storageService;

    public ResponseEntity<Resource> stream(Song song, String rangeHeader) {
        if (rangeHeader == null || rangeHeader.isBlank()) {
            InputStream stream = storageService.download(song.getStorageKey());
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(song.getContentType()))
                    .contentLength(song.getSizeBytes())
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .body(new InputStreamResource(stream));
        }

        Matcher matcher = RANGE_PATTERN.matcher(rangeHeader);
        if (!matcher.matches()) {
            throw new InvalidRangeException("Invalid Range header");
        }

        long start = Long.parseLong(matcher.group(1));
        String endGroup = matcher.group(2);
        long end = endGroup == null || endGroup.isBlank() ? song.getSizeBytes() - 1 : Long.parseLong(endGroup);

        if (start < 0 || end < start || start >= song.getSizeBytes()) {
            throw new InvalidRangeException("Requested range is not satisfiable");
        }

        long boundedEnd = Math.min(end, song.getSizeBytes() - 1);
        long length = boundedEnd - start + 1;
        InputStream stream = storageService.downloadRange(song.getStorageKey(), start, length);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaType.parseMediaType(song.getContentType()))
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .header(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + boundedEnd + "/" + song.getSizeBytes())
                .contentLength(length)
                .body(new InputStreamResource(stream));
    }
}