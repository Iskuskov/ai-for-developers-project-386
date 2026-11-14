package com.callcalendar;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * Фолбэк /error: неизвестный путь для API-клиента даёт 404 { code: "NOT_FOUND" }; навигация
 * браузера без собранной статики не зацикливается и остаётся 404. Со собранной статикой
 * HTML-навигация получает index.html (проверяется в Docker-прогоне).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class SpaFallbackApiTest {

  @Autowired private RestTestClient client;

  @Test
  void unknownApiPathReturnsJsonNotFound() {
    var response =
        client
            .get()
            .uri("/no-such-api-path")
            .header("Accept", "application/json")
            .exchange()
            .expectStatus()
            .isNotFound()
            .expectBody(String.class)
            .returnResult();
    assertTrue(
        response.getResponseBody() != null && response.getResponseBody().contains("NOT_FOUND"));
  }

  @Test
  void unknownHtmlNavigationWithoutBuiltSpaStaysNotFoundWithoutLoop() {
    var response =
        client
            .get()
            .uri("/some/ui/route")
            .header("Accept", "text/html")
            .exchange()
            .expectStatus()
            .isNotFound()
            .expectBody(String.class)
            .returnResult();
    assertTrue(
        response.getResponseBody() == null || response.getResponseBody().contains("NOT_FOUND"));
  }
}
