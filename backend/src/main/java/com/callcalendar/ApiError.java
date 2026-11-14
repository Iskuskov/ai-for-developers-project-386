package com.callcalendar;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/** Единый формат ошибок API. */
@Schema(description = "Единый формат ошибок API.")
public record ApiError(
    @NotNull
        @Schema(description = "Машинный код ошибки.", requiredMode = Schema.RequiredMode.REQUIRED)
        String code,
    @NotNull
        @Schema(
            description = "Понятное человеку сообщение.",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String message) {}
