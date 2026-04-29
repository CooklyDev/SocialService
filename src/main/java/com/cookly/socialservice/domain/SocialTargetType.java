package com.cookly.socialservice.domain;

public enum SocialTargetType {
  RECIPE("recipe"),
  COLLECTION("collection");

  private final String resourceName;

  SocialTargetType(String resourceName) {
    this.resourceName = resourceName;
  }

  public String resourceName() {
    return resourceName;
  }
}
