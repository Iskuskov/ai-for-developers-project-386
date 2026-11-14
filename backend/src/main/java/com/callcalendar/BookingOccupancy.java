package com.callcalendar;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Занятые интервалы Записей; шов для хранилища Записей, которое появится вместе с POST /bookings.
 */
public interface BookingOccupancy {

  /** Полуинтервал [start, end), занятый Записью; касание в точке не конфликт. */
  record Interval(LocalDateTime start, LocalDateTime end) {

    boolean overlapsWith(LocalDateTime otherStart, LocalDateTime otherEnd) {
      return start.isBefore(otherEnd) && otherStart.isBefore(end);
    }
  }

  /** Интервалы Записей, приходящиеся на указанную дату. */
  List<Interval> intervalsOn(LocalDate date);
}
