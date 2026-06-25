package com.spoffy.musiccloud.controller;

import com.spoffy.musiccloud.domain.User;
import com.spoffy.musiccloud.dto.playlist.AddSongToPlaylistRequest;
import com.spoffy.musiccloud.dto.playlist.CreatePlaylistRequest;
import com.spoffy.musiccloud.dto.playlist.PlaylistResponse;
import com.spoffy.musiccloud.dto.playlist.RenamePlaylistRequest;
import com.spoffy.musiccloud.service.PlaylistService;
import com.spoffy.musiccloud.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/playlists")
@RequiredArgsConstructor
@Tag(name = "Playlists", description = "Gestión de playlists personales.")
@SecurityRequirement(name = "bearerAuth")
public class PlaylistController {

    private final PlaylistService playlistService;
    private final UserService userService;

    @PostMapping
    @Operation(summary = "Crear playlist", description = "Crea una playlist con nombre de ejemplo ya visible en Swagger.")
    @ApiResponse(responseCode = "200", description = "Playlist creada", content = @Content(schema = @Schema(implementation = PlaylistResponse.class)))
    public PlaylistResponse create(@Valid @RequestBody CreatePlaylistRequest request, Authentication authentication) {
        User user = userService.getByEmail(authentication.getName());
        return playlistService.create(request, user);
    }

    @GetMapping("/my")
    @Operation(summary = "Listar mis playlists", description = "Devuelve todas las playlists del usuario autenticado.")
    public List<PlaylistResponse> myPlaylists(Authentication authentication) {
        User user = userService.getByEmail(authentication.getName());
        return playlistService.myPlaylists(user);
    }

    @PostMapping("/{id}/songs")
    @Operation(summary = "Agregar canción a playlist", description = "Usa el UUID de una canción existente. El ejemplo está prellenado.")
    @ApiResponse(responseCode = "200", description = "Playlist actualizada", content = @Content(schema = @Schema(implementation = PlaylistResponse.class)))
    public PlaylistResponse addSong(@PathVariable UUID id, @Valid @RequestBody AddSongToPlaylistRequest request, Authentication authentication) {
        User user = userService.getByEmail(authentication.getName());
        return playlistService.addSong(id, request, user);
    }

    @DeleteMapping("/{id}/songs/{songId}")
    @Operation(summary = "Quitar canción de playlist", description = "Elimina una canción solo de la playlist del usuario.")
    public PlaylistResponse removeSong(@PathVariable UUID id, @PathVariable UUID songId, Authentication authentication) {
        User user = userService.getByEmail(authentication.getName());
        return playlistService.removeSong(id, songId, user);
    }

    @PostMapping("/{id}/rename")
    @Operation(summary = "Renombrar playlist", description = "Actualiza el nombre visible de la playlist.")
    public PlaylistResponse rename(@PathVariable UUID id, @Valid @RequestBody RenamePlaylistRequest request, Authentication authentication) {
        User user = userService.getByEmail(authentication.getName());
        return playlistService.rename(id, request, user);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar playlist", description = "Elimina la playlist completa del usuario autenticado.")
    public ResponseEntity<Void> delete(@PathVariable UUID id, Authentication authentication) {
        User user = userService.getByEmail(authentication.getName());
        playlistService.deletePlaylist(id, user);
        return ResponseEntity.noContent().build();
    }
}