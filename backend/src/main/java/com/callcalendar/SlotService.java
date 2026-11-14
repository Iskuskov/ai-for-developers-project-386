package com.callcalendar;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Вычисление свободных Слотов Типа встречи на дату: рабочие часы минус занятые интервалы Записей.
 */
@Service
public class SlotService {

  static final ZoneId WORK_ZONE = ZoneId.of("Europe/Moscow");
  static final LocalTime WORK_START = LocalTime.of(9, 0);
  static final LocalTime WORK_END = LocalTime.of(18, 0);
  static final Duration GRID_STEP = Duration.ofMinutes(30);
  static final int BOOKING_WINDOW_DAYS = 14;
  static final DateTimeFormatter START_FORMAT =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

  private final Clock clock;
  private final BookingOccupancy bookings;

  SlotService(Clock clock, BookingOccupancy bookings) {
    this.clock = clock;
    this.bookings = bookings;
  }

  List<Slot> slotsOf(EventType eventType, LocalDate date) {
    LocalDateTime now = clock.instant().atZone(WORK_ZONE).toLocalDateTime();
    LocalDate today = now.toLocalDate();
    boolean inWindow = !date.isBefore(today) && date.isBefore(today.plusDays(BOOKING_WINDOW_DAYS));
    // Дата вне 14-дневного окна записи (в прошлом или за его пределами) — слотов нет вовсе
    if (!inWindow) {
      return List.of();
    }
    Duration duration = Duration.ofMinutes(eventType.durationMinutes());
    List<BookingOccupancy.Interval> occupied = bookings.intervalsOn(date);
    List<Slot> slots = new ArrayList<>();
    for (LocalDateTime start = date.atTime(WORK_START);
        !start.toLocalTime().plus(duration).isAfter(WORK_END);
        start = start.plus(GRID_STEP)) {
      boolean past = start.isBefore(now);
      boolean taken = isTaken(occupied, start, duration);
      boolean available = inWindow && !past && !taken;
      slots.add(new Slot(start.format(START_FORMAT), available));
    }
    return slots;
  }

  private static boolean isTaken(
      List<BookingOccupancy.Interval> occupied, LocalDateTime start, Duration duration) {
    return occupied.stream()
        .anyMatch(interval -> interval.overlapsWith(start, start.plus(duration)));
  }
}
