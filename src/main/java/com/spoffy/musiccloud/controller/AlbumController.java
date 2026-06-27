package com.spoffy.musiccloud.controller;

import com.spoffy.musiccloud.domain.Album;
import com.spoffy.musiccloud.dto.song.SongResponse;
import com.spoffy.musiccloud.service.AlbumService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
@Tag(name = "Albums", description = "Gestión de álbumes y sus canciones.")
// Temporary for tests. Restore when JWT protection is needed:
// @SecurityRequirement(name = "bearerAuth")
public class AlbumController {

    private final AlbumService albumService;    

    @GetMapping("/news")
    @Operation(summary = "Obtener nuevos albumnes agregados", description = "Devuelve la metadata de los nuevos álbumes agregados.")
    @ApiResponse(responseCode = "200", description = "Lista de nuevos álbumes", content = @Content(schema = @Schema(implementation = SongResponse.class)))
    public List<Album> getNewAlbums() {
        return albumService.getNewAlbums();
    }

}