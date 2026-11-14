package com.callcalendar;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Занятые интервалы Записей и занятие новых: шов между расчётом свободных Слотов, созданием Записей
 * и хранилищем Записей.
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

  /** Предстоящие Записи всех Типов встреч (старт не раньше now) по возрастанию времени старта. */
  List<Booking> findUpcoming(LocalDateTime now);

  /**
   * Атомарно занимает полуинтервал [старт Записи, старт + duration) новой Записью: если он не
   * пересекается с уже занятыми интервалами, Запись сохраняется и возвращается true; иначе
   * состояние не меняется и возвращается false.
   */
  boolean reserve(Booking booking, Duration duration);
}
