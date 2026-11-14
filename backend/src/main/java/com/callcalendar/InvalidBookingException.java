package com.callcalendar;

/** Некорректные данные Записи: машиночитаемый код и сообщение для человека. */
public class InvalidBookingException extends RuntimeException {

  private final String code;

  InvalidBookingException(String code, String message) {
    super(message);
    this.code = code;
  }

  String getCode() {
    return code;
  }
}
