package com.callcalendar;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Часы для расчёта «сейчас»; в тестах подменяются фиксированными. */
@Configuration
public class TimeConfig {

  @Bean
  Clock clock() {
    return Clock.systemDefaultZone();
  }
}
