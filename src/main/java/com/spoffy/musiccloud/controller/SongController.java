package com.spoffy.musiccloud.controller;

import com.spoffy.musiccloud.domain.User;
import com.spoffy.musiccloud.dto.song.SongResponse;
import com.spoffy.musiccloud.dto.song.SongSearchResponse;
import com.spoffy.musiccloud.dto.song.SongUploadRequest;
import com.spoffy.musiccloud.dto.song.SongUploadForm;
import com.spoffy.musiccloud.service.SongService;
import com.spoffy.musiccloud.service.StreamingService;
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
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/songs")
@RequiredArgsConstructor
@Tag(name = "Songs", description = "Subida, consulta, búsqueda y streaming de música.")
@SecurityRequirement(name = "bearerAuth")
public class SongController {

    private final SongService songService;
    private final StreamingService streamingService;
    private final UserService userService;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    @Operation(summary = "Subir canción", description = "Formulario multipart con título, artista, tags y archivo. Swagger debe mostrarlo como campos de formulario, no como JSON.")
    @ApiResponse(responseCode = "200", description = "Canción registrada", content = @Content(schema = @Schema(implementation = SongResponse.class)))
    @ApiResponse(responseCode = "401", description = "Falta autenticación JWT")
    @ApiResponse(responseCode = "403", description = "JWT inválido o sin permisos")
    public SongResponse upload(@Valid @ModelAttribute SongUploadRequest request,
                               Authentication authentication) {
        User user = userService.getByEmail(authentication.getName());
        SongUploadForm form = new SongUploadForm(request.title(), request.artist(), request.tags());
        return songService.upload(request.file(), form, user);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener canción", description = "Devuelve la metadata de una canción por UUID.")
    @ApiResponse(responseCode = "200", description = "Canción encontrada", content = @Content(schema = @Schema(implementation = SongResponse.class)))
    public SongResponse getSong(@PathVariable UUID id) {
        return songService.getSong(id);
    }

    @GetMapping("/stream/{id}")
    @Operation(summary = "Streaming de canción", description = "Sirve el audio completo o por rango HTTP para reproducción parcial.")
    public ResponseEntity<Resource> stream(@PathVariable UUID id, @RequestHeader(value = "Range", required = false) String rangeHeader) {
        return streamingService.stream(songService.getRequiredSong(id), rangeHeader);
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar canciones", description = "Busca por título, artista o tags. Si no envías filtros, devuelve todas las canciones.")
    @ApiResponse(responseCode = "200", description = "Lista de resultados", content = @Content(schema = @Schema(implementation = SongSearchResponse.class)))
    public List<SongSearchResponse> search(@RequestParam(required = false) String query,
                                           @RequestParam(required = false) String artist,
                                           @RequestParam(required = false) String tag) {
        return songService.search(query, artist, tag);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar canción", description = "Solo ADMIN. Borra la metadata y el objeto en MinIO.")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        songService.deleteSong(id);
        return ResponseEntity.noContent().build();
    }
}