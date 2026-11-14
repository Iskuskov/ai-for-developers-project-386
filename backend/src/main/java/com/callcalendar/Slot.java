package com.callcalendar;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/** Слот: старт на 30-минутной сетке рабочих часов и признак доступности для записи. */
@Schema(
    description =
        "Слот: старт на 30-минутной сетке рабочих часов и признак доступности для записи.")
public record Slot(
    @NotNull
        @Schema(
            description =
                "Дата и время старта слота, ISO 8601 (YYYY-MM-DDTHH:mm:ss), время Europe/Moscow.",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String start,
    @Schema(
            description = "Доступен ли слот для записи.",
            requiredMode = Schema.RequiredMode.REQUIRED)
        boolean available) {}
