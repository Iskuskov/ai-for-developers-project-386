package com.callcalendar;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Просмотр Типов встреч по контракту Call Calendar API. */
@RestController
@Tag(name = "EventTypes")
public class EventTypeController {

  @Operation(
      operationId = "listEventTypes",
      description = "Список типов встреч, доступных для записи.")
  @ApiResponse(
      responseCode = "200",
      description = "The request has succeeded.",
      useReturnTypeSchema = true)
  @GetMapping(value = "/event-types", produces = "application/json")
  public List<EventType> list() {
    return EventType.seed();
  }
}
