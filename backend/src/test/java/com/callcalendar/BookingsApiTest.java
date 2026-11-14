package com.callcalendar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * Интеграционный тест шва HTTP API: POST /bookings создаёт Запись по контракту; интервалы Записей —
 * полуинтервалы [старт, конец), касание не конфликт, при параллельных запросах на один слот успех
 * ровно у одного; занятое время — 409 { code: "SLOT_TAKEN" }, ошибки данных — 400 { code, message
 * }.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class BookingsApiTest {

  /** «Сейчас» для всех сценариев: 20 января 2026, 12:00 Europe/Moscow. */
  private static final Instant NOW = Instant.parse("2026-01-20T09:00:00Z");

  @Autowired private RestTestClient client;

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
  void createBookingReturnsCreatedRecordWithId() {
    nowIs(NOW);
    client
        .post()
        .uri("/bookings")
        .body(bookingFor("consultation", "2026-01-21T10:00:00"))
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody()
        .jsonPath("$.id")
        .isNotEmpty()
        .jsonPath("$.eventTypeId")
        .isEqualTo("consultation")
        .jsonPath("$.slotStart")
        .isEqualTo("2026-01-21T10:00:00")
        .jsonPath("$.guestName")
        .isEqualTo("Александр")
        .jsonPath("$.guestEmail")
        .isEqualTo("guest@example.com");
  }

  @Test
  void bookingTheSameSlotAgainIsRejectedWithSlotTaken() {
    nowIs(NOW);
    booking("consultation", "2026-01-25T12:00:00", 201);
    client
        .post()
        .uri("/bookings")
        .body(bookingFor("consultation", "2026-01-25T12:00:00"))
        .exchange()
        .expectStatus()
        .isEqualTo(409)
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("SLOT_TAKEN")
        .jsonPath("$.message")
        .isEqualTo("Это время уже занято, выберите другое");
  }

  @Test
  void bookingStartingWhenAnotherEndsIsAllowed() {
    nowIs(NOW);
    // «Ревью кода» занимает 10:00–11:00; старт в момент окончания другой Записи — не конфликт
    booking("code-review", "2026-01-22T10:00:00", 201);
    booking("consultation", "2026-01-22T11:00:00", 201);
  }

  @Test
  void partiallyOverlappingBookingOfAnotherEventTypeIsRejected() {
    nowIs(NOW);
    // «Ревью кода» 10:00–11:00; «Консультация» 10:30–11:00 пересекается с ним
    booking("code-review", "2026-01-23T10:00:00", 201);
    booking("consultation", "2026-01-23T10:30:00", 409);
  }

  @Test
  void bookingCoveringStartOfExistingOneIsRejected() {
    nowIs(NOW);
    // «Консультация» 09:30–10:00; «Ревью кода» 09:00–10:00 накрывает её интервал
    booking("consultation", "2026-01-24T09:30:00", 201);
    booking("code-review", "2026-01-24T09:00:00", 409);
  }

  @ParameterizedTest
  @CsvSource({
    "2026-01-21T10:10:00, INVALID_SLOT_START",
    "not-a-date, INVALID_SLOT_START",
    "2026-01-20T09:00:00, SLOT_IN_PAST",
    "2026-01-21T18:00:00, SLOT_OUTSIDE_WORKING_HOURS",
    "2026-02-03T10:00:00, SLOT_OUTSIDE_BOOKING_WINDOW",
  })
  void invalidSlotStartReturnsUnifiedError(String slotStart, String expectedCode) {
    nowIs(NOW);
    client
        .post()
        .uri("/bookings")
        .body(bookingFor("consultation", slotStart))
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo(expectedCode)
        .jsonPath("$.message")
        .isNotEmpty();
  }

  @Test
  void invalidGuestEmailReturnsUnifiedError() {
    nowIs(NOW);
    client
        .post()
        .uri("/bookings")
        .body(
            Map.of(
                "eventTypeId", "consultation",
                "slotStart", "2026-01-21T10:00:00",
                "guestName", "Александр",
                "guestEmail", "not-an-email"))
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("INVALID_GUEST_EMAIL")
        .jsonPath("$.message")
        .isNotEmpty();
  }

  @Test
  void blankGuestNameReturnsContractError() {
    nowIs(NOW);
    client
        .post()
        .uri("/bookings")
        .body(
            Map.of(
                "eventTypeId", "consultation",
                "slotStart", "2026-01-21T10:00:00",
                "guestName", "  ",
                "guestEmail", "guest@example.com"))
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("VALIDATION_FAILED")
        .jsonPath("$.message")
        .isNotEmpty();
  }

  @Test
  void unknownEventTypeIdReturnsUnifiedError() {
    nowIs(NOW);
    client
        .post()
        .uri("/bookings")
        .body(bookingFor("nope", "2026-01-21T10:00:00"))
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo("EVENT_TYPE_NOT_FOUND")
        .jsonPath("$.message")
        .isNotEmpty();
  }

  @Test
  void concurrentBookingsOfSameSlotSucceedExactlyOnce() throws Exception {
    nowIs(NOW);
    try (ExecutorService executor = Executors.newFixedThreadPool(5)) {
      List<Integer> statuses =
          IntStream.range(0, 5)
              .mapToObj(attempt ->
                  CompletableFuture.supplyAsync(
                      () ->
                          client
                              .post()
                              .uri("/bookings")
                              .body(bookingFor("consultation", "2026-01-26T10:00:00"))
                              .exchange()
                              .returnResult()
                              .getStatus()
                              .value(),
                      executor))
              .toList()
              .stream()
              .map(CompletableFuture::join)
              .toList();
      executor.shutdown();
      assertThat(statuses.stream().filter(status -> status == 201).count()).isEqualTo(1);
      assertThat(statuses.stream().filter(status -> status == 409).count()).isEqualTo(4);
    }
  }

  private void booking(String eventTypeId, String slotStart, int expectedStatus) {
    client
        .post()
        .uri("/bookings")
        .body(bookingFor(eventTypeId, slotStart))
        .exchange()
        .expectStatus()
        .isEqualTo(expectedStatus);
  }
}
