package com.cookly.socialservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
  public static final String SESSION_SECURITY_SCHEME = "X-Session-ID";

  @Bean
  public OpenAPI cooklyOpenApi() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Cookly Social Service")
                .description("API for ratings, comments, and favorites")
                .version("1.0"))
        .components(
            new Components()
                .addSecuritySchemes(
                    SESSION_SECURITY_SCHEME,
                    new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .name("X-Session-ID")
                        .description("Session identifier")));
  }
}
