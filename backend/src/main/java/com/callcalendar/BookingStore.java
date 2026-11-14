package com.callcalendar;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Component;

/**
 * In-memory хранилище Записей: занятые интервалы [старт, конец) всех Типов встреч за жизнь
 * процесса. reserve синхронизирован — при параллельных запросах атомарно проверяет непересечение и
 * сохраняет.
 */
@Component
public class BookingStore implements BookingOccupancy {

  private record StoredBooking(Booking booking, Interval interval) {}

  private final List<StoredBooking> items = new CopyOnWriteArrayList<>();

  @Override
  public List<Interval> intervalsOn(LocalDate date) {
    return items.stream()
        .filter(stored -> stored.interval().start().toLocalDate().equals(date))
        .map(StoredBooking::interval)
        .toList();
  }

  /** Предстоящие Записи всех Типов встреч (старт не раньше now) по возрастанию времени старта. */
  @Override
  public List<Booking> findUpcoming(LocalDateTime now) {
    return items.stream()
        .filter(stored -> !stored.interval().start().isBefore(now))
        .sorted(Comparator.comparing(stored -> stored.interval().start()))
        .map(StoredBooking::booking)
        .toList();
  }

  @Override
  public synchronized boolean reserve(Booking booking, Duration duration) {
    LocalDateTime start = booking.slotStart();
    Interval candidate = new Interval(start, start.plus(duration));
    boolean overlapping =
        items.stream()
            .map(StoredBooking::interval)
            .anyMatch(interval -> interval.overlapsWith(candidate.start(), candidate.end()));
    if (overlapping) {
      return false;
    }
    items.add(new StoredBooking(booking, candidate));
    return true;
  }
}
