package com.callcalendar;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/** Запись: подтверждённое бронирование слота гостем. */
@Schema(description = "Запись: подтверждённое бронирование слота гостем.")
public record Booking(
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
        String guestEmail) {}
