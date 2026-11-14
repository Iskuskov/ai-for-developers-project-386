package com.callcalendar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * Интеграционный тест шва HTTP API: GET /bookings возвращает предстоящие Записи всех Типов встреч
 * одним списком по возрастанию времени старта; прошедшие Записи не показываются.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ListBookingsApiTest {

  /** «Сейчас» для всех сценариев: 20 января 2026, 12:00 Europe/Moscow. */
  private static final Instant NOW = Instant.parse("2026-01-20T09:00:00Z");

  @Autowired private RestTestClient client;

  @Autowired private BookingStore bookingStore;

  @MockitoBean private Clock clock;

  private void nowIs(Instant now) {
    lenient().when(clock.instant()).thenReturn(now);
  }

  private Map<String, String> bookingFor(String eventTypeId, String slotStart) {
    return Map.of(
        "eventTypeId",
        eventTypeId,
        "slotStart",
        slotStart,
        "guestName",
        "Александр",
        "guestEmail",
        "guest@example.com");
  }

  @Test
  @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
  void listReturnsEmptyListWhenNoBookingsExist() {
    nowIs(NOW);
    client.get().uri("/bookings").exchange().expectStatus().isOk().expectBody().json("[]");
  }

  @Test
  @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
  void listShowsBookingCreatedByGuest() {
    nowIs(NOW);
    booking("consultation", "2026-01-21T10:00:00");
    client
        .get()
        .uri("/bookings")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.length()")
        .isEqualTo(1)
        .jsonPath("$[0].eventTypeId")
        .isEqualTo("consultation")
        .jsonPath("$[0].slotStart")
        .isEqualTo("2026-01-21T10:00:00")
        .jsonPath("$[0].guestName")
        .isEqualTo("Александр")
        .jsonPath("$[0].guestEmail")
        .isEqualTo("guest@example.com")
        .jsonPath("$[0].id")
        .isNotEmpty();
  }

  @Test
  @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
  void listHidesPastBookings() {
    nowIs(NOW);
    seedPastBooking("2026-01-19T10:00:00");
    // Старт ровно «сейчас» — Запись ещё предстоящая
    booking("greeting", "2026-01-20T12:00:00");
    client
        .get()
        .uri("/bookings")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.length()")
        .isEqualTo(1)
        .jsonPath("$[0].slotStart")
        .isEqualTo("2026-01-20T12:00:00");
  }

  @Test
  @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
  void listIsSortedBySlotStartAscending() {
    nowIs(NOW);
    booking("code-review", "2026-01-22T11:00:00");
    booking("consultation", "2026-01-21T10:00:00");
    booking("greeting", "2026-01-23T09:30:00");
    client
        .get()
        .uri("/bookings")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.length()")
        .isEqualTo(3)
        .jsonPath("$[0].slotStart")
        .isEqualTo("2026-01-21T10:00:00")
        .jsonPath("$[1].slotStart")
        .isEqualTo("2026-01-22T11:00:00")
        .jsonPath("$[2].slotStart")
        .isEqualTo("2026-01-23T09:30:00");
  }

  /** Тестовая фикстура: заносит прошедшую Запись напрямую в store, минуя валидацию POST. */
  private void seedPastBooking(String slotStart) {
    Booking past =
        new Booking(
            "past-1",
            "consultation",
            LocalDateTime.parse(slotStart, SlotService.START_FORMAT),
            "Мария",
            "past-guest@example.com");
    boolean reserved = bookingStore.reserve(past, Duration.ofMinutes(30));
    assertThat(reserved)
        .as("фикстура: прошедшая Запись должна сохраниться без конфликтов")
        .isTrue();
  }

  private void booking(String eventTypeId, String slotStart) {
    client
        .post()
        .uri("/bookings")
        .body(bookingFor(eventTypeId, slotStart))
        .exchange()
        .expectStatus()
        .isCreated();
  }
}
