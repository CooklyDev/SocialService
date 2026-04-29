package com.cookly.socialservice;

import com.cookly.socialservice.config.ExternalServicesProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ExternalServicesProperties.class)
public class SocialServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(SocialServiceApplication.class, args);
  }
}
