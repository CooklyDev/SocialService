package com.cookly.socialservice.controller;

import com.cookly.socialservice.dto.ApiErrorResponse;
import com.cookly.socialservice.exception.ExternalServiceException;
import com.cookly.socialservice.exception.InvalidInputException;
import com.cookly.socialservice.exception.TargetNotFoundException;
import com.cookly.socialservice.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(UnauthorizedException.class)
  ResponseEntity<ApiErrorResponse> handleUnauthorized(
      UnauthorizedException exception, HttpServletRequest request) {
    return error(HttpStatus.UNAUTHORIZED, exception.getMessage(), request);
  }

  @ExceptionHandler(MissingRequestHeaderException.class)
  ResponseEntity<ApiErrorResponse> handleMissingSessionHeader(
      MissingRequestHeaderException exception, HttpServletRequest request) {
    return error(HttpStatus.UNAUTHORIZED, "Unauthorized", request);
  }

  @ExceptionHandler(TargetNotFoundException.class)
  ResponseEntity<ApiErrorResponse> handleNotFound(
      TargetNotFoundException exception, HttpServletRequest request) {
    return error(HttpStatus.NOT_FOUND, exception.getMessage(), request);
  }

  @ExceptionHandler({
    InvalidInputException.class,
    ConstraintViolationException.class,
    HttpMessageNotReadableException.class
  })
  ResponseEntity<ApiErrorResponse> handleBadRequest(Exception exception, HttpServletRequest request) {
    return error(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ApiErrorResponse> handleValidation(
      MethodArgumentNotValidException exception, HttpServletRequest request) {
    String message =
        exception.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(this::fieldErrorMessage)
            .orElse("Invalid request");
    return error(HttpStatus.BAD_REQUEST, message, request);
  }

  @ExceptionHandler(ExternalServiceException.class)
  ResponseEntity<ApiErrorResponse> handleExternalService(
      ExternalServiceException exception, HttpServletRequest request) {
    return error(HttpStatus.BAD_GATEWAY, exception.getMessage(), request);
  }

  @ExceptionHandler(Exception.class)
  ResponseEntity<ApiErrorResponse> handleUnexpected(Exception exception, HttpServletRequest request) {
    return error(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", request);
  }

  private String fieldErrorMessage(FieldError fieldError) {
    String message = fieldError.getDefaultMessage();
    if (message == null || message.isBlank()) {
      return "Invalid " + fieldError.getField();
    }
    return fieldError.getField() + ": " + message;
  }

  private ResponseEntity<ApiErrorResponse> error(
      HttpStatus status, String message, HttpServletRequest request) {
    return ResponseEntity.status(status)
        .body(new ApiErrorResponse(status.value(), message, request.getRequestURI(), Instant.now()));
  }
}
