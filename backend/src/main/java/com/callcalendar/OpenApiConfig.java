package com.callcalendar;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Метаданные OpenAPI-документа, совпадающие со спекой, сгенерированной из TypeSpec. */
@Configuration
public class OpenApiConfig {

  @Bean
  OpenAPI callCalendarOpenApi() {
    return new OpenAPI()
        .info(new Info().title("Call Calendar API").version("1.0.0"))
        .tags(List.of(new Tag().name("EventTypes")));
  }
}
