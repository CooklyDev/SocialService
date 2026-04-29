package com.cookly.socialservice.dto;

import java.time.Instant;

public record ApiErrorResponse(int statusCode, String message, String path, Instant timestamp) {}
