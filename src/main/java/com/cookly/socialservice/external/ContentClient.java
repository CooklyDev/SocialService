package com.cookly.socialservice.external;

import com.cookly.socialservice.config.ExternalServicesProperties;
import com.cookly.socialservice.domain.SocialTargetType;
import com.cookly.socialservice.exception.ExternalServiceException;
import com.cookly.socialservice.exception.TargetNotFoundException;
import com.cookly.socialservice.exception.UnauthorizedException;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Component
public class ContentClient {
  private final RestClient restClient;
  private final ExternalServicesProperties properties;

  public ContentClient(RestClient.Builder restClientBuilder, ExternalServicesProperties properties) {
    this.restClient = restClientBuilder.build();
    this.properties = properties;
  }

  public void ensureAccessible(SocialTargetType targetType, UUID targetId, String sessionId) {
    if (!properties.content().validationEnabled()) {
      return;
    }

    String template =
        targetType == SocialTargetType.RECIPE
            ? properties.content().recipeEndpointTemplate()
            : properties.content().collectionEndpointTemplate();
    String url = ExternalServiceUrl.targetUrl(properties.content().url(), template, targetId);

    try {
      restClient.get().uri(url).header("X-Session-ID", sessionId).retrieve().toBodilessEntity();
    } catch (RestClientResponseException exception) {
      HttpStatus status = HttpStatus.resolve(exception.getStatusCode().value());
      if (status == HttpStatus.UNAUTHORIZED || status == HttpStatus.FORBIDDEN) {
        throw new UnauthorizedException();
      }
      if (status == HttpStatus.NOT_FOUND || status == HttpStatus.BAD_REQUEST) {
        throw new TargetNotFoundException(targetType.resourceName());
      }
      throw new ExternalServiceException("Content service is unavailable", exception);
    } catch (RestClientException exception) {
      throw new ExternalServiceException("Content service is unavailable", exception);
    }
  }
}
