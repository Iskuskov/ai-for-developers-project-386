package com.callcalendar;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * Запись в контракте Call Calendar API: доменная Запись, у которой старт переведён в строку формата
 * yyyy-MM-dd'T'HH:mm:ss. Имя схемы закреплено как Booking, чтобы OpenAPI не зависел от класса-шва.
 */
@Schema(name = "Booking", description = "Запись: подтверждённое бронирование слота гостем.")
public record BookingResponse(
    @NotNull
        @Schema(description = "Идентификатор записи.", requiredMode = Schema.RequiredMode.REQUIRED)
        String id,
    @NotNull
        @Schema(
            description = "Идентификатор типа встречи.",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String eventTypeId,
    @NotNull
        @Schema(
            description =
                "Дата и время старта записи, ISO 8601 (YYYY-MM-DDTHH:mm:ss), время Europe/Moscow.",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String slotStart,
    @NotNull @Schema(description = "Имя гостя.", requiredMode = Schema.RequiredMode.REQUIRED)
        String guestName,
    @NotNull @Schema(description = "Email гостя.", requiredMode = Schema.RequiredMode.REQUIRED)
        String guestEmail) {

  /** Отображение доменной Записи на контракт: LocalDateTime → строка формата контракта. */
  public static BookingResponse from(Booking booking) {
    return new BookingResponse(
        booking.id(),
        booking.eventTypeId(),
        booking.slotStart().format(SlotService.START_FORMAT),
        booking.guestName(),
        booking.guestEmail());
  }
}
