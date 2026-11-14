package com.callcalendar;

/** Слот уже занят другой Записью. */
public class SlotTakenException extends RuntimeException {

  SlotTakenException() {
    super("Это время уже занято, выберите другое");
  }
}
