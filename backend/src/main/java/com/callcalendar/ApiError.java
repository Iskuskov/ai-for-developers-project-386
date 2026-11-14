package com.callcalendar;

import io.swagger.v3.oas.annotations.media.Schema;

/** Ошибка запроса: машиночитаемый код и сообщение для человека. */
@Schema(description = "Ошибка запроса: машиночитаемый код и сообщение для человека.")
public record ApiError(
    @Schema(description = "Машиночитаемый код ошибки.", requiredMode = Schema.RequiredMode.REQUIRED)
        String code,
    @Schema(
            description = "Сообщение об ошибке для человека.",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String message) {}
