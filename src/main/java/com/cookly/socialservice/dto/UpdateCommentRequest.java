package com.cookly.socialservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCommentRequest(
    @Schema(description = "Comment text", example = "Updated comment")
        @NotBlank
        @Size(max = 5000)
        String text) {}
