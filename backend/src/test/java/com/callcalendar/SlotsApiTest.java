package com.callcalendar;

import static org.mockito.Mockito.lenient;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * Интеграционный тест шва HTTP API: GET /slots отдаёт сетку 30-минутных стартов с признаком
 * занятости — рабочие часы 09:00–18:00 Europe/Moscow, прошедшие сегодня слоты недоступны, слот
 * длится как Тип встречи и целиком помещается в рабочие часы.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class SlotsApiTest {

  /** «Сейчас» для всех сценариев: 20 января 2026, 12:00 Europe/Moscow. */
  private static final Instant NOW = Instant.parse("2026-01-20T09:00:00Z");

  private static final LocalDate TODAY = LocalDate.of(2026, 1, 20);

  private static final LocalDate LATER_DATE = LocalDate.of(2026, 1, 22);

  @Autowired private RestTestClient client;

  @MockitoBean private Clock clock;

  @MockitoBean private BookingOccupancy bookings;

  private void nowIs(Instant now) {
    lenient().when(clock.instant()).thenReturn(now);
  }

  @Test
  void futureDateReturnsThirtyMinuteGridInsideWorkingHours() {
    nowIs(NOW);
    client
        .get()
        .uri("/slots?eventTypeId=consultation&date=" + LATER_DATE)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.length()")
        .isEqualTo(18)
        .jsonPath("$[0].start")
        .isEqualTo("2026-01-22T09:00:00")
        .jsonPath("$[0].available")
        .isEqualTo(true)
        .jsonPath("$[1].start")
        .isEqualTo("2026-01-22T09:30:00")
        .jsonPath("$[17].start")
        .isEqualTo("2026-01-22T17:30:00")
        .jsonPath("$[17].available")
        .isEqualTo(true);
  }

  @Test
  void pastSlotsTodayAreUnavailable() {
    nowIs(NOW);
    client
        .get()
        .uri("/slots?eventTypeId=consultation&date=" + TODAY)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.length()")
        .isEqualTo(18)
        .jsonPath("$[0].start")
        .isEqualTo("2026-01-20T09:00:00")
        .jsonPath("$[0].available")
        .isEqualTo(false)
        .jsonPath("$[5].start")
        .isEqualTo("2026-01-20T11:30:00")
        .jsonPath("$[5].available")
        .isEqualTo(false)
        .jsonPath("$[6].start")
        .isEqualTo("2026-01-20T12:00:00")
        .jsonPath("$[6].available")
        .isEqualTo(true);
  }

  @Test
  void slotCrossingWorkingHoursEndIsAbsent() {
    nowIs(NOW);
    // 60-минутный «Ревью кода»: последний старт — 17:00; слот 17:30–18:30 в рабочие часы не
    // помещается
    client
        .get()
        .uri("/slots?eventTypeId=code-review&date=" + LATER_DATE)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.length()")
        .isEqualTo(17)
        .jsonPath("$[16].start")
        .isEqualTo("2026-01-22T17:00:00");
  }

  @Test
  void dateBeyondBookingWindowReturnsEmptyList() {
    nowIs(NOW);
    // окно записи — 14 дней начиная с сегодняшнего: 2026-02-03 уже за пределами
    client
        .get()
        .uri("/slots?eventTypeId=consultation&date=2026-02-03")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .json("[]");
  }

  @Test
  void pastDateReturnsEmptyList() {
    nowIs(NOW);
    client
        .get()
        .uri("/slots?eventTypeId=consultation&date=2026-01-19")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .json("[]");
  }

  @Test
  void unknownEventTypeIdIsRejectedWithUnifiedErrorBody() {
    nowIs(NOW);
    client
        .get()
        .uri("/slots?eventTypeId=nope&date=" + LATER_DATE)
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
  void occupiedBookingIntervalMarksOverlappingSlotsUnavailable() {
    nowIs(NOW);
    lenient()
        .when(bookings.intervalsOn(LATER_DATE))
        .thenReturn(
            List.of(
                new BookingOccupancy.Interval(LATER_DATE.atTime(10, 0), LATER_DATE.atTime(11, 0))));
    client
        .get()
        .uri("/slots?eventTypeId=consultation&date=" + LATER_DATE)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$[1].start")
        .isEqualTo("2026-01-22T09:30:00")
        .jsonPath("$[1].available")
        .isEqualTo(true)
        .jsonPath("$[2].start")
        .isEqualTo("2026-01-22T10:00:00")
        .jsonPath("$[2].available")
        .isEqualTo(false)
        .jsonPath("$[3].start")
        .isEqualTo("2026-01-22T10:30:00")
        .jsonPath("$[3].available")
        .isEqualTo(false)
        .jsonPath("$[4].start")
        .isEqualTo("2026-01-22T11:00:00")
        .jsonPath("$[4].available")
        .isEqualTo(true);
  }
}
