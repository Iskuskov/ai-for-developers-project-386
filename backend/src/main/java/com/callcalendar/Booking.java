package com.callcalendar;

import java.time.LocalDateTime;

/** Запись: подтверждённое бронирование слота гостем; старт — календарное время Europe/Moscow. */
public record Booking(
    String id, String eventTypeId, LocalDateTime slotStart, String guestName, String guestEmail) {}
