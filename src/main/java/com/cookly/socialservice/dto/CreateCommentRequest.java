package com.cookly.socialservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCommentRequest(
    @Schema(description = "Comment text", example = "Great recipe")
        @NotBlank
        @Size(max = 5000)
        String text) {}
