package com.cookly.socialservice.exception;

public class UnauthorizedException extends RuntimeException {
  public UnauthorizedException() {
    super("Unauthorized");
  }
}
