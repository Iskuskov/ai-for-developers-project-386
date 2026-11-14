package com.callcalendar;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * Интеграционный тест шва HTTP API: POST /event-types создаёт Тип встречи по контракту, ошибки
 * данных возвращает как 400 { code, message }.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class CreateEventTypesApiTest {

  @Autowired private RestTestClient client;

  @Test
  void createEventTypeReturnsCreatedItemWithIdAndShowsItInList() {
    client
        .post()
        .uri("/event-types")
        .body(
            Map.of(
                "title", "Планирование",
                "description", "Совместное планирование задач.",
                "durationMinutes", 45))
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody()
        .jsonPath("$.id")
        .isNotEmpty()
        .jsonPath("$.title")
        .isEqualTo("Планирование")
        .jsonPath("$.description")
        .isEqualTo("Совместное планирование задач.")
        .jsonPath("$.durationMinutes")
        .isEqualTo(45);

    client
        .get()
        .uri("/event-types")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.length()")
        .isEqualTo(4)
        .jsonPath("$[3].title")
        .isEqualTo("Планирование")
        .jsonPath("$[3].durationMinutes")
        .isEqualTo(45);
  }

  @Test
  void createEventTypeWithBlankTitleReturnsContractError() {
    client
        .post()
        .uri("/event-types")
        .body(
            Map.of(
                "title", "  ",
                "description", "Совместное планирование задач.",
                "durationMinutes", 30))
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("VALIDATION_FAILED")
        .jsonPath("$.message")
        .isNotEmpty();
  }

  @ParameterizedTest
  @ValueSource(ints = {0, -15})
  void createEventTypeWithNonPositiveDurationReturnsContractError(int durationMinutes) {
    client
        .post()
        .uri("/event-types")
        .body(
            Map.of(
                "title", "Планирование",
                "description", "Совместное планирование задач.",
                "durationMinutes", durationMinutes))
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("VALIDATION_FAILED")
        .jsonPath("$.message")
        .isNotEmpty();
  }
}
