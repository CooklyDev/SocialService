# Cookly Social Service

Java Spring Boot service for social features around recipes and collections:
ratings, comments, and favorites.

## Runtime dependencies

The service validates sessions through Auth Service and validates recipe or
collection accessibility through Content Service.

Configuration is read from `.env` by Docker Compose:

- `AUTH_URL`
- `AUTH_SESSION_RESOLVE_ENDPOINT`
- `CONTENT_URL`
- `CONTENT_RECIPE_ENDPOINT_TEMPLATE`
- `CONTENT_COLLECTION_ENDPOINT_TEMPLATE`
- `CONTENT_VALIDATION_ENABLED`

## Run

```bash
docker compose up --build
```

The API is exposed on `APP_PORT` from `.env`; default is
`http://localhost:3005`.

Useful endpoints:

- `GET /actuator/health`
- `GET /docs`
- `GET /api-docs`

All social endpoints require `X-Session-ID`.

## Main API

Recipe endpoints:

- `PUT /api/v1/recipes/{recipeId}/ratings/me`
- `GET /api/v1/recipes/{recipeId}/ratings/me`
- `DELETE /api/v1/recipes/{recipeId}/ratings/me`
- `PUT /api/v1/recipes/{recipeId}/favorites/me`
- `DELETE /api/v1/recipes/{recipeId}/favorites/me`
- `POST /api/v1/recipes/{recipeId}/comments`
- `GET /api/v1/recipes/{recipeId}/comments`
- `GET /api/v1/recipes/{recipeId}/social`

Collection endpoints mirror the same shape under `/api/v1/collections`.

Current user endpoints:

- `GET /api/v1/me/favorites/recipes`
- `GET /api/v1/me/favorites/collections`
- `GET /api/v1/me/comments/recipes`
- `GET /api/v1/me/comments/collections`
