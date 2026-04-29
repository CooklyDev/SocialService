package com.cookly.socialservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpsertRatingRequest(
    @Schema(description = "Rating value from 1 to 5", example = "5")
        @NotNull
        @Min(1)
        @Max(5)
        Integer value) {}
