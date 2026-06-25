package com.spoffy.musiccloud.service;

import com.spoffy.musiccloud.domain.Playlist;
import com.spoffy.musiccloud.domain.PlaylistSong;
import com.spoffy.musiccloud.domain.Song;
import com.spoffy.musiccloud.domain.User;
import com.spoffy.musiccloud.dto.playlist.AddSongToPlaylistRequest;
import com.spoffy.musiccloud.dto.playlist.CreatePlaylistRequest;
import com.spoffy.musiccloud.dto.playlist.PlaylistResponse;
import com.spoffy.musiccloud.dto.playlist.PlaylistSongResponse;
import com.spoffy.musiccloud.dto.playlist.RenamePlaylistRequest;
import com.spoffy.musiccloud.exception.ResourceNotFoundException;
import com.spoffy.musiccloud.repository.PlaylistRepository;
import com.spoffy.musiccloud.repository.PlaylistSongRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistSongRepository playlistSongRepository;
    private final SongService songService;

    @Transactional
    public PlaylistResponse create(CreatePlaylistRequest request, User user) {
        Playlist playlist = new Playlist();
        playlist.setName(request.name());
        playlist.setUser(user);
        return toResponse(playlistRepository.save(playlist));
    }

    public List<PlaylistResponse> myPlaylists(User user) {
        return playlistRepository.findByUserId(user.getId()).stream().map(this::toResponse).toList();
    }

    @Transactional
    public PlaylistResponse rename(UUID playlistId, RenamePlaylistRequest request, User user) {
        Playlist playlist = getOwnedPlaylist(playlistId, user);
        playlist.setName(request.name());
        return toResponse(playlistRepository.save(playlist));
    }

    @Transactional
    public PlaylistResponse addSong(UUID playlistId, AddSongToPlaylistRequest request, User user) {
        Playlist playlist = getOwnedPlaylist(playlistId, user);
        Song song = songService.getRequiredSong(request.songId());
        playlistSongRepository.findByPlaylistIdAndSongId(playlist.getId(), song.getId())
                .orElseGet(() -> {
                    PlaylistSong playlistSong = playlistSongRepository.save(createPlaylistSong(playlist, song));
                    playlist.getPlaylistSongs().add(playlistSong);
                    return playlistSong;
                });
        return toResponse(playlistRepository.findById(playlistId).orElseThrow());
    }

    @Transactional
    public PlaylistResponse removeSong(UUID playlistId, UUID songId, User user) {
        Playlist playlist = getOwnedPlaylist(playlistId, user);
        playlistSongRepository.deleteByPlaylistIdAndSongId(playlist.getId(), songId);
        playlist.getPlaylistSongs().removeIf(playlistSong -> playlistSong.getSong().getId().equals(songId));
        return toResponse(playlistRepository.findById(playlistId).orElseThrow());
    }

    @Transactional
    public void deletePlaylist(UUID playlistId, User user) {
        Playlist playlist = getOwnedPlaylist(playlistId, user);
        playlistRepository.delete(playlist);
    }

    private PlaylistSong createPlaylistSong(Playlist playlist, Song song) {
        PlaylistSong playlistSong = new PlaylistSong();
        playlistSong.setPlaylist(playlist);
        playlistSong.setSong(song);
        return playlistSong;
    }

    private Playlist getOwnedPlaylist(UUID playlistId, User user) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));
        if (!playlist.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Playlist not found");
        }
        return playlist;
    }

    private PlaylistResponse toResponse(Playlist playlist) {
        List<PlaylistSongResponse> songs = playlist.getPlaylistSongs().stream()
                .map(playlistSong -> new PlaylistSongResponse(playlistSong.getSong().getId(), playlistSong.getSong().getTitle(), playlistSong.getSong().getArtist()))
                .toList();
        return new PlaylistResponse(playlist.getId(), playlist.getName(), playlist.getUser().getId(), playlist.getCreatedAt(), songs);
    }
}