package com.cookly.socialservice.external;

import com.cookly.socialservice.config.ExternalServicesProperties;
import com.cookly.socialservice.domain.AuthenticatedUser;
import com.cookly.socialservice.exception.ExternalServiceException;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Component
public class AuthClient {
  private final RestClient restClient;
  private final ExternalServicesProperties properties;

  public AuthClient(RestClient.Builder restClientBuilder, ExternalServicesProperties properties) {
    this.restClient = restClientBuilder.build();
    this.properties = properties;
  }

  public Optional<AuthenticatedUser> resolve(String sessionId) {
    if (!StringUtils.hasText(sessionId)) {
      return Optional.empty();
    }

    var form = new LinkedMultiValueMap<String, String>();
    form.add("session_id", sessionId);

    try {
      ResolveSessionResponse response =
          restClient
              .post()
              .uri(
                  ExternalServiceUrl.join(
                      properties.auth().url(), properties.auth().sessionResolveEndpoint()))
              .contentType(MediaType.APPLICATION_FORM_URLENCODED)
              .body(form)
              .retrieve()
              .body(ResolveSessionResponse.class);

      if (response == null || !response.success() || response.data() == null) {
        return Optional.empty();
      }

      UUID userId = response.data().userId();
      UUID resolvedSessionId = response.data().sessionId();
      if (userId == null || resolvedSessionId == null) {
        return Optional.empty();
      }

      return Optional.of(new AuthenticatedUser(userId, resolvedSessionId));
    } catch (RestClientResponseException exception) {
      if (exception.getStatusCode().is4xxClientError()) {
        return Optional.empty();
      }
      throw new ExternalServiceException("Auth service is unavailable", exception);
    } catch (RestClientException exception) {
      throw new ExternalServiceException("Auth service is unavailable", exception);
    }
  }

  private record ResolveSessionResponse(boolean success, ResolveSessionData data) {}

  private record ResolveSessionData(
      @JsonProperty("UserID") UUID userId, @JsonProperty("SessionID") UUID sessionId) {}
}
