package com.cookly.socialservice.external;

import java.util.UUID;

final class ExternalServiceUrl {
  private ExternalServiceUrl() {}

  static String join(String baseUrl, String path) {
    String normalizedBase = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    String normalizedPath = path.startsWith("/") ? path : "/" + path;
    return normalizedBase + normalizedPath;
  }

  static String targetUrl(String baseUrl, String template, UUID targetId) {
    return join(baseUrl, template.replace("{id}", targetId.toString()));
  }
}
