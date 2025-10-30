package edu.dosw.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimeConfig {
  /**
   * Provides a Clock instance for time-related operations
   *
   * @return Clock instance using the system default time zone
   */
  @Bean
  public Clock clock() {
    return Clock.systemDefaultZone();
  }
}
