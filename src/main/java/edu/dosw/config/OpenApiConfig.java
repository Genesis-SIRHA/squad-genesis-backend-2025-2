package edu.dosw.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
  /**
   * Configures custom OpenAPI documentation with JWT security scheme
   *
   * @return OpenAPI instance with API information and security configuration
   */
  @Bean
  public OpenAPI customOpenAPI() {
    final String securitySchemeName = "bearerAuth";

    SecurityScheme securityScheme =
        new SecurityScheme()
            .name(securitySchemeName)
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT");

    SecurityRequirement securityRequirement = new SecurityRequirement().addList(securitySchemeName);

    return new OpenAPI()
        .info(
            new Info()
                .title("SIRHA API")
                .version("1.0")
                .description("University Management System API"))
        .addSecurityItem(securityRequirement)
        .components(new Components().addSecuritySchemes(securitySchemeName, securityScheme));
  }
}
