package com.cookly.socialservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cookly")
public record ExternalServicesProperties(Auth auth, Content content) {
  public record Auth(String url, String sessionResolveEndpoint) {}

  public record Content(
      String url,
      String recipeEndpointTemplate,
      String collectionEndpointTemplate,
      boolean validationEnabled) {}
}
