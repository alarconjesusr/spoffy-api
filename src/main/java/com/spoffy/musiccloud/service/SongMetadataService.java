package com.spoffy.musiccloud.service;

import com.spoffy.musiccloud.domain.Song;
import com.spoffy.musiccloud.domain.SongAudioMetadata;
import com.spoffy.musiccloud.repository.SongAudioMetadataRepository;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class SongMetadataService {

    private final SongAudioMetadataRepository songAudioMetadataRepository;

    public ExtractedSongMetadata extractFromFile(MultipartFile file) {
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("spoffy-audio-meta-", resolveTempSuffix(file));
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }

            AudioFile audioFile = AudioFileIO.read(tempFile.toFile());
            Tag tag = audioFile.getTag();
            AudioHeader audioHeader = audioFile.getAudioHeader();

            String checksumMd5 = computeMd5(tempFile);
            String sourceFilename = file.getOriginalFilename();
            Long sourceFileSizeBytes = file.getSize();
            String sourceMimeType = file.getContentType();
            String sourceFileExtension = readNormalizedExtension(sourceFilename);

            String title = readTag(tag, FieldKey.TITLE);
            String artist = readTag(tag, FieldKey.ARTIST);
            String album = readTag(tag, FieldKey.ALBUM);
            String genre = readTag(tag, FieldKey.GENRE);
            Integer year = parseFirstInteger(readTag(tag, FieldKey.YEAR));
            String comment = readTag(tag, FieldKey.COMMENT);
            String description = readTag(tag, FieldKey.LYRICS);
            Integer trackNumber = parseFirstInteger(readTag(tag, FieldKey.TRACK));
            Integer discNumber = parseFirstInteger(readTag(tag, FieldKey.DISC_NO));
            String isrc = readTag(tag, FieldKey.ISRC);
            String languageCode = readTag(tag, FieldKey.LANGUAGE);
            String lyrics = readTag(tag, FieldKey.LYRICS);
            Integer bpm = parseFirstInteger(readTag(tag, FieldKey.BPM));
            String musicalKey = readTag(tag, FieldKey.KEY);

            String codec = audioHeader == null ? null : audioHeader.getFormat();
            Integer bitrateKbps = parseFirstInteger(audioHeader == null ? null : audioHeader.getBitRate());
            Integer sampleRateHz = parseFirstInteger(audioHeader == null ? null : audioHeader.getSampleRate());
                String channelMode = audioHeader == null ? null : normalize(audioHeader.getChannels());
                Integer channels = parseChannelCount(channelMode);
            Integer durationSec = audioHeader == null ? null : audioHeader.getTrackLength();
            Integer durationMs = durationSec == null ? null : durationSec * 1000;
                Artwork artwork = tag == null ? null : tag.getFirstArtwork();
                boolean hasEmbeddedArtwork = artwork != null;
                String pictureMimeType = artwork == null ? null : normalize(artwork.getMimeType());
                String pictureType = artwork == null ? null : normalize(String.valueOf(artwork.getPictureType()));
                String pictureDescription = artwork == null ? null : normalize(artwork.getDescription());
                byte[] pictureData = artwork == null ? null : artwork.getBinaryData();
                Integer pictureSizeBytes = pictureData == null ? null : pictureData.length;

            return new ExtractedSongMetadata(
                    checksumMd5,
                    sourceFilename,
                    sourceFileSizeBytes,
                    sourceMimeType,
                    sourceFileExtension,
                    title,
                    artist,
                    album,
                    genre,
                    year,
                    comment,
                    description,
                    trackNumber,
                    discNumber,
                    isrc,
                    languageCode,
                    lyrics,
                    bpm,
                    musicalKey,
                    codec,
                    bitrateKbps,
                    sampleRateHz,
                    channels,
                    channelMode,
                    durationSec,
                    durationMs,
                    hasEmbeddedArtwork,
                    pictureMimeType,
                    pictureType,
                    pictureDescription,
                    pictureSizeBytes,
                    pictureData,
                    Instant.now());
        } catch (Exception ignored) {
            return ExtractedSongMetadata.empty();
        } finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException ignored) {
                    // Best effort cleanup for temporary metadata extraction file.
                }
            }
        }
    }

    @Transactional
    public void saveMetadata(Song song, ExtractedSongMetadata extracted) {
        if (song == null || extracted == null || !extracted.hasData()) {
            return;
        }

        SongAudioMetadata metadata = songAudioMetadataRepository.findBySongId(song.getId())
                .orElseGet(SongAudioMetadata::new);
        metadata.setSong(song);
        metadata.setChecksumMd5(extracted.checksumMd5());
        metadata.setSourceFilename(extracted.sourceFilename());
        metadata.setSourceFileSizeBytes(extracted.sourceFileSizeBytes());
        metadata.setSourceMimeType(extracted.sourceMimeType());
        metadata.setSourceFileExtension(extracted.sourceFileExtension());
        metadata.setDetectedTitle(extracted.title());
        metadata.setDetectedArtist(extracted.artist());
        metadata.setDetectedAlbum(extracted.album());
        metadata.setDetectedGenre(extracted.genre());
        metadata.setDetectedYear(extracted.year());
        metadata.setDetectedComment(extracted.comment());
        metadata.setDetectedDescription(extracted.description());
        metadata.setDetectedTrackNumber(extracted.trackNumber());
        metadata.setDetectedDiscNumber(extracted.discNumber());
        metadata.setCodec(extracted.codec());
        metadata.setBitrateKbps(extracted.bitrateKbps());
        metadata.setSampleRateHz(extracted.sampleRateHz());
        metadata.setChannels(extracted.channels());
        metadata.setChannelMode(extracted.channelMode());
        metadata.setDurationSec(extracted.durationSec());
        metadata.setDurationMs(extracted.durationMs());
        metadata.setBpm(extracted.bpm());
        metadata.setMusicalKey(extracted.musicalKey());
        metadata.setHasEmbeddedArtwork(extracted.hasEmbeddedArtwork());
        metadata.setPictureMimeType(extracted.pictureMimeType());
        metadata.setPictureType(extracted.pictureType());
        metadata.setPictureDescription(extracted.pictureDescription());
        metadata.setPictureSizeBytes(extracted.pictureSizeBytes());
        metadata.setExtractedAt(extracted.extractedAt());
        songAudioMetadataRepository.save(metadata);
    }

    private String readTag(Tag tag, FieldKey fieldKey) {
        if (tag == null) {
            return null;
        }
        return normalize(tag.getFirst(fieldKey));
    }

    private Integer parseFirstInteger(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String numeric = value.trim().split("/")[0].replaceAll("[^0-9]", "");
        if (numeric.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(numeric);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Integer parseChannelCount(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.trim().toLowerCase();
        if (normalized.contains("mono")) {
            return 1;
        }
        if (normalized.contains("stereo")) {
            return 2;
        }
        return parseFirstInteger(value);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String computeMd5(Path filePath) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] bytes = Files.readAllBytes(filePath);
            byte[] hash = digest.digest(bytes);
            StringBuilder builder = new StringBuilder();
            for (byte b : hash) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (IOException | NoSuchAlgorithmException ex) {
            return null;
        }
    }

    private String readNormalizedExtension(String originalFilename) {
        if (originalFilename == null) {
            return null;
        }
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex >= originalFilename.length() - 1) {
            return null;
        }
        String extension = originalFilename.substring(dotIndex).toLowerCase(Locale.ROOT);
        return normalizeAudioExtension(extension);
    }

    private String resolveTempSuffix(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex >= 0 && dotIndex < originalFilename.length() - 1) {
                String extension = originalFilename.substring(dotIndex).toLowerCase(Locale.ROOT);
                if (extension.matches("\\.[a-z0-9]{1,10}")) {
                    return normalizeAudioExtension(extension);
                }
            }
        }

        String contentType = file.getContentType();
        if (contentType == null) {
            return ".mp3";
        }

        return switch (contentType.toLowerCase(Locale.ROOT)) {
            case "audio/mpeg", "audio/mp3", "audio/x-mp3" -> ".mp3";
            case "audio/flac", "audio/x-flac" -> ".flac";
            case "audio/wav", "audio/x-wav", "audio/wave" -> ".wav";
            case "audio/aac" -> ".aac";
            case "audio/ogg" -> ".ogg";
            case "audio/mp4", "audio/x-m4a" -> ".m4a";
            default -> ".mp3";
        };
    }

    private String normalizeAudioExtension(String extension) {
        return switch (extension) {
            case ".mpeg", ".mp2" -> ".mp3";
            case ".wave" -> ".wav";
            case ".oga" -> ".ogg";
            default -> extension;
        };
    }

    public record ExtractedSongMetadata(
            String checksumMd5,
            String sourceFilename,
            Long sourceFileSizeBytes,
            String sourceMimeType,
            String sourceFileExtension,
            String title,
            String artist,
            String album,
            String genre,
            Integer year,
            String comment,
            String description,
            Integer trackNumber,
            Integer discNumber,
            String isrc,
            String languageCode,
            String lyrics,
            Integer bpm,
            String musicalKey,
            String codec,
            Integer bitrateKbps,
            Integer sampleRateHz,
            Integer channels,
                String channelMode,
            Integer durationSec,
            Integer durationMs,
            boolean hasEmbeddedArtwork,
                String pictureMimeType,
                String pictureType,
                String pictureDescription,
                Integer pictureSizeBytes,
            byte[] pictureData,
            Instant extractedAt) {

        static ExtractedSongMetadata empty() {
            return new ExtractedSongMetadata(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                        null,
                    null,
                    null,
                    false,
                        null,
                        null,
                        null,
                        null,
                    null,
                    null);
        }

        boolean hasData() {
                    return checksumMd5 != null
                        || sourceFilename != null
                        || sourceFileSizeBytes != null
                        || sourceMimeType != null
                        || sourceFileExtension != null
                        || title != null
                    || artist != null
                    || album != null
                    || genre != null
                        || year != null
                        || comment != null
                        || description != null
                    || trackNumber != null
                    || discNumber != null
                    || isrc != null
                    || languageCode != null
                    || lyrics != null
                    || bpm != null
                    || musicalKey != null
                    || codec != null
                    || bitrateKbps != null
                    || sampleRateHz != null
                    || channels != null
                    || channelMode != null
                    || durationSec != null
                    || durationMs != null
                    || pictureMimeType != null
                    || pictureType != null
                    || pictureDescription != null
                    || pictureSizeBytes != null
                    || (pictureData != null && pictureData.length > 0)
                    || hasEmbeddedArtwork;
        }

        boolean hasPictureData() {
            return pictureData != null && pictureData.length > 0;
        }
    }
}