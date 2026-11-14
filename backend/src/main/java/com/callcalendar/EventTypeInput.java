package com.callcalendar;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** Данные для создания типа встречи: название, описание и длительность. */
@Schema(description = "Данные для создания типа встречи: название, описание и длительность.")
public record EventTypeInput(
    @NotBlank(message = "Название не может быть пустым")
        @Schema(description = "Название типа встречи.", requiredMode = Schema.RequiredMode.REQUIRED)
        String title,
    @NotNull(message = "Описание не может отсутствовать")
        @Schema(description = "Описание для гостей.", requiredMode = Schema.RequiredMode.REQUIRED)
        String description,
    @Positive(message = "Длительность должна быть положительным числом минут")
        @Schema(
            description = "Длительность звонка в минутах.",
            requiredMode = Schema.RequiredMode.REQUIRED)
        int durationMinutes) {}
