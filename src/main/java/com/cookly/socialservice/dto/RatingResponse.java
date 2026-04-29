package com.cookly.socialservice.dto;

import com.cookly.socialservice.domain.SocialTargetType;
import java.time.Instant;
import java.util.UUID;

public record RatingResponse(
    UUID id,
    UUID userId,
    SocialTargetType targetType,
    UUID targetId,
    int value,
    Instant createdAt,
    Instant updatedAt) {}
