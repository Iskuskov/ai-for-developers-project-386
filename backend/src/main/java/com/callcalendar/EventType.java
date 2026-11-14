package com.callcalendar;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/** Тип встречи: вид звонка с названием, описанием и длительностью. */
@Schema(description = "Вид звонка: название, описание и длительность.")
public record EventType(
    @NotNull
        @Schema(
            description = "Идентификатор типа встречи.",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String id,
    @NotNull
        @Schema(description = "Название типа встречи.", requiredMode = Schema.RequiredMode.REQUIRED)
        String title,
    @NotNull
        @Schema(description = "Описание для гостей.", requiredMode = Schema.RequiredMode.REQUIRED)
        String description,
    @Schema(
            description = "Длительность звонка в минутах.",
            requiredMode = Schema.RequiredMode.REQUIRED)
        int durationMinutes) {

  /** Seed-список Типов встреч, доступных Гостям до появления POST /event-types. */
  public static List<EventType> seed() {
    return List.of(
        new EventType(
            "greeting",
            "Знакомство",
            "Короткий звонок, чтобы познакомиться и понять, чем можно помочь.",
            15),
        new EventType(
            "consultation", "Консультация", "Разбор вопроса или задачи в течение получаса.", 30),
        new EventType(
            "code-review", "Ревью кода", "Обсуждение кода и правок по итогам ревью.", 60));
  }
}
