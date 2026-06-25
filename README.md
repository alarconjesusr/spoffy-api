# Spoffy

Backend de nube privada de música con Spring Boot, JWT, PostgreSQL y MinIO.

## Stack

- Spring Boot 3
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL
- MinIO para objetos de audio
- Docker Compose

## Arranque local

1. Levanta dependencias:

```bash
docker compose up -d postgres minio minio-init
```

2. Ejecuta la API:

```bash
mvn spring-boot:run
```

## Endpoints

Swagger UI queda disponible en:

- `/swagger-ui/index.html`
- `/v3/api-docs`

Los DTOs incluyen ejemplos por defecto para que Swagger sugiera valores reales como email, contraseña, nombre de playlist y tags de canción.

### Auth

- `POST /auth/register`
- `POST /auth/login`

### Songs

- `POST /songs/upload`
- `GET /songs/{id}`
- `GET /songs/stream/{id}`
- `GET /songs/search`

### Playlists

- `POST /playlists`
- `GET /playlists/my`
- `POST /playlists/{id}/songs`
- `DELETE /playlists/{id}/songs/{songId}`

### Admin

- `DELETE /songs/{id}`
- `GET /users`
