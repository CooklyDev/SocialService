package com.cookly.socialservice.dto;

import com.cookly.socialservice.domain.SocialTargetType;
import java.util.UUID;

public record SocialSummaryResponse(
    UUID targetId,
    SocialTargetType targetType,
    long ratingCount,
    Double averageRating,
    long commentCount,
    long favoriteCount,
    Integer myRating,
    boolean favoriteByMe) {}
