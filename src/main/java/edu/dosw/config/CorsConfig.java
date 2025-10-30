package edu.dosw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
  /**
   * Creates and configures a CORS configuration for the application
   *
   * @return WebMvcConfigurer instance with CORS settings
   */
  @Bean
  public WebMvcConfigurer corsConfigurer() {
    /**
     * Configures CORS mappings for specific URL patterns
     *
     * @param registry The CORS registry to add mappings to
     */
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry
            .addMapping("/public/**")
            .allowedOriginPatterns(
                "http://localhost:3000",
                "http://localhost:3001",
                "http://localhost:5174",
                "http://127.0.0.1:3000",
                "http://127.0.0.1:3001",
                "http://127.0.0.1:5174")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("Origin", "Content-Type", "Accept", "Authorization", "X-Requested-With")
            .allowCredentials(true)
            .maxAge(3600);
      }
    };
  }
}
