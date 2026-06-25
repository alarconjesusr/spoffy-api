package com.spoffy.musiccloud.controller;

import com.spoffy.musiccloud.dto.user.UserResponse;
import com.spoffy.musiccloud.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Operaciones administrativas.")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar usuarios", description = "Solo ADMIN. Devuelve todos los usuarios registrados.")
    @ApiResponse(responseCode = "200", description = "Usuarios listados", content = @Content(schema = @Schema(implementation = UserResponse.class)))
    public List<UserResponse> users() {
        return userService.listUsers();
    }
}