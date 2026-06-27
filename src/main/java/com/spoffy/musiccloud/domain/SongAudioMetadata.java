package com.spoffy.musiccloud.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "song_audio_metadata")
public class SongAudioMetadata {

    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne
    @JoinColumn(name = "song_id", nullable = false, unique = true)
    private Song song;

    @Column
    private String checksumMd5;

    @Column
    private String sourceFilename;

    @Column
    private Long sourceFileSizeBytes;

    @Column
    private String sourceMimeType;

    @Column(length = 16)
    private String sourceFileExtension;

    @Column
    private String detectedTitle;

    @Column
    private String detectedArtist;

    @Column
    private String detectedAlbum;

    @Column
    private String detectedGenre;

    @Column
    private Integer detectedYear;

    @Column(length = 8000)
    private String detectedComment;

    @Column(length = 8000)
    private String detectedDescription;

    @Column
    private Integer detectedTrackNumber;

    @Column
    private Integer detectedDiscNumber;

    @Column
    private String codec;

    @Column
    private Integer bitrateKbps;

    @Column
    private Integer sampleRateHz;

    @Column
    private Integer channels;

    @Column
    private String channelMode;

    @Column
    private Integer durationMs;

    @Column
    private Integer durationSec;

    @Column
    private Double loudnessLufs;

    @Column
    private Integer bpm;

    @Column(length = 16)
    private String musicalKey;

    @Column(nullable = false)
    private boolean hasEmbeddedArtwork = false;

    @Column
    private String pictureMimeType;

    @Column
    private String pictureType;

    @Column
    private String pictureDescription;

    @Column
    private Integer pictureSizeBytes;

    @Column
    private Instant extractedAt;
}