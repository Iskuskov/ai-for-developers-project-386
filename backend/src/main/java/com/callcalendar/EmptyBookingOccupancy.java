package com.callcalendar;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Component;

/** Пока хранилища Записей нет, занятых интервалов не бывает: все слоты на сетке свободны. */
@Component
public class EmptyBookingOccupancy implements BookingOccupancy {

  @Override
  public List<Interval> intervalsOn(LocalDate date) {
    return List.of();
  }
}
