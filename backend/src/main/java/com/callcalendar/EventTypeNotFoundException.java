package com.callcalendar;

/** Запрошенного Типа встречи не существует. */
public class EventTypeNotFoundException extends RuntimeException {

  EventTypeNotFoundException(String eventTypeId) {
    super("Тип встречи не найден: " + eventTypeId);
  }
}
