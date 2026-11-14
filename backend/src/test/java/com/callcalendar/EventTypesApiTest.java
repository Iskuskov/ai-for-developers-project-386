package com.callcalendar;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * Интеграционный тест шва HTTP API: GET /event-types отдаёт seed-список Типов встреч по контракту;
 * GET /event-types/{id} — один Тип встречи, а для неизвестного идентификатора — 404 { code, message
 * }.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class EventTypesApiTest {

  @Autowired private RestTestClient client;

  @Test
  void listEventTypesReturnsSeedItemsWithContractFields() {
    client
        .get()
        .uri("/event-types")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.length()")
        .isEqualTo(3)
        .jsonPath("$[0].id")
        .isEqualTo("greeting")
        .jsonPath("$[0].title")
        .isEqualTo("Знакомство")
        .jsonPath("$[0].description")
        .isEqualTo("Короткий звонок, чтобы познакомиться и понять, чем можно помочь.")
        .jsonPath("$[0].durationMinutes")
        .isEqualTo(15)
        .jsonPath("$[1].id")
        .isEqualTo("consultation")
        .jsonPath("$[1].title")
        .isEqualTo("Консультация")
        .jsonPath("$[1].description")
        .isEqualTo("Разбор вопроса или задачи в течение получаса.")
        .jsonPath("$[1].durationMinutes")
        .isEqualTo(30)
        .jsonPath("$[2].id")
        .isEqualTo("code-review")
        .jsonPath("$[2].title")
        .isEqualTo("Ревью кода")
        .jsonPath("$[2].description")
        .isEqualTo("Обсуждение кода и правок по итогам ревью.")
        .jsonPath("$[2].durationMinutes")
        .isEqualTo(60);
  }

  @Test
  void getEventTypeReturnsTypeById() {
    client
        .get()
        .uri("/event-types/consultation")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.id")
        .isEqualTo("consultation")
        .jsonPath("$.title")
        .isEqualTo("Консультация")
        .jsonPath("$.description")
        .isEqualTo("Разбор вопроса или задачи в течение получаса.")
        .jsonPath("$.durationMinutes")
        .isEqualTo(30);
  }

  @Test
  void unknownEventTypeIdReturns404WithUnifiedErrorBody() {
    client
        .get()
        .uri("/event-types/nope")
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("EVENT_TYPE_NOT_FOUND")
        .jsonPath("$.message")
        .isNotEmpty();
  }
}
