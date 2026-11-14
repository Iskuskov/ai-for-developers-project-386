package com.callcalendar;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

/**
 * Создание Записи Гостя: валидация данных и слота, затем занятие интервала через шов занятости
 * Записей. Метод синхронизирован — при параллельных запросах на один слот успех ровно у одного.
 */
@Service
public class BookingService {

  private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

  private final Clock clock;

  private final BookingOccupancy bookings;

  BookingService(Clock clock, BookingOccupancy bookings) {
    this.clock = clock;
    this.bookings = bookings;
  }

  synchronized Booking create(EventType eventType, BookingInput input) {
    LocalDateTime start = parseSlotStart(input.slotStart());
    ensureOnGrid(start);
    LocalDateTime now = clock.instant().atZone(SlotService.WORK_ZONE).toLocalDateTime();
    ensureNotPast(start, now);
    ensureInsideWorkingHours(eventType, start);
    ensureInsideBookingWindow(start, now);
    ensureValidEmail(input.guestEmail());
    Booking booking =
        new Booking(
            UUID.randomUUID().toString(),
            eventType.id(),
            input.slotStart(),
            input.guestName(),
            input.guestEmail());
    if (!bookings.reserve(booking, Duration.ofMinutes(eventType.durationMinutes()))) {
      throw new SlotTakenException();
    }
    return booking;
  }

  private static LocalDateTime parseSlotStart(String slotStart) {
    try {
      return LocalDateTime.parse(slotStart, SlotService.START_FORMAT);
    } catch (RuntimeException exception) {
      throw new InvalidBookingException(
          "INVALID_SLOT_START",
          "Старт слота должен быть датой и временем формата " + "YYYY-MM-DDTHH:mm:ss");
    }
  }

  private static void ensureOnGrid(LocalDateTime start) {
    if (start.getSecond() != 0 || start.getMinute() % 30 != 0) {
      throw new InvalidBookingException(
          "INVALID_SLOT_START", "Старт слота должен быть на 30-минутной сетке");
    }
  }

  private static void ensureNotPast(LocalDateTime start, LocalDateTime now) {
    if (start.isBefore(now)) {
      throw new InvalidBookingException("SLOT_IN_PAST", "Нельзя записаться на прошедшее время");
    }
  }

  private static void ensureInsideWorkingHours(EventType eventType, LocalDateTime start) {
    Duration duration = Duration.ofMinutes(eventType.durationMinutes());
    if (start.toLocalTime().isBefore(SlotService.WORK_START)
        || start.plus(duration).toLocalTime().isAfter(SlotService.WORK_END)) {
      throw new InvalidBookingException(
          "SLOT_OUTSIDE_WORKING_HOURS", "Запись должна целиком помещаться в рабочие часы");
    }
  }

  private static void ensureInsideBookingWindow(LocalDateTime start, LocalDateTime now) {
    LocalDate startDay = start.toLocalDate();
    LocalDate today = now.toLocalDate();
    if (startDay.isBefore(today)
        || !startDay.isBefore(today.plusDays(SlotService.BOOKING_WINDOW_DAYS))) {
      throw new InvalidBookingException(
          "SLOT_OUTSIDE_BOOKING_WINDOW", "Запись доступна только на ближайшие 14 дней");
    }
  }

  private static void ensureValidEmail(String guestEmail) {
    if (!EMAIL_PATTERN.matcher(guestEmail).matches()) {
      throw new InvalidBookingException("INVALID_GUEST_EMAIL", "Некорректный email");
    }
  }
}
