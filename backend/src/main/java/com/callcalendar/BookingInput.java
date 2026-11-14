package com.callcalendar;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Данные для создания записи: тип встречи, старт слота, имя и email гостя. */
@Schema(description = "Данные для создания записи: тип встречи, старт слота, имя и email гостя.")
public record BookingInput(
    @NotBlank(message = "Тип встречи не может отсутствовать")
        @Schema(
            description = "Идентификатор типа встречи.",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String eventTypeId,
    @NotBlank(message = "Старт слота не может отсутствовать")
        @Schema(
            description =
                "Дата и время старта слота, ISO 8601 (YYYY-MM-DDTHH:mm:ss), время Europe/Moscow.",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String slotStart,
    @NotBlank(message = "Имя не может быть пустым")
        @Size(min = 1, max = 100, message = "Имя не может быть длиннее 100 символов")
        @Schema(description = "Имя гостя.", requiredMode = Schema.RequiredMode.REQUIRED)
        String guestName,
    @NotBlank(message = "Email не может отсутствовать")
        @Schema(description = "Email гостя.", requiredMode = Schema.RequiredMode.REQUIRED)
        String guestEmail) {}
