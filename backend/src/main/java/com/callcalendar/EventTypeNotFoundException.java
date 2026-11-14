package com.callcalendar;

import org.springframework.http.HttpStatus;

/**
 * Запрошенного Типа встречи не существует. Статус зависит от шва: ссылка на неизвестный Тип внутри
 * запроса (/slots, /bookings) — 400, прямое чтение GET /event-types/{id} — 404.
 */
public class EventTypeNotFoundException extends RuntimeException {

  private final HttpStatus status;

  EventTypeNotFoundException(String eventTypeId) {
    this(eventTypeId, HttpStatus.BAD_REQUEST);
  }

  EventTypeNotFoundException(String eventTypeId, HttpStatus status) {
    super("Тип встречи не найден: " + eventTypeId);
    this.status = status;
  }

  HttpStatus status() {
    return status;
  }
}
