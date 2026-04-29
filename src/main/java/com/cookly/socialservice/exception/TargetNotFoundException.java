package com.cookly.socialservice.exception;

public class TargetNotFoundException extends RuntimeException {
  public TargetNotFoundException(String targetName) {
    super(targetName + " not found");
  }
}
