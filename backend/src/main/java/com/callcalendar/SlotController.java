package com.callcalendar;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Просмотр свободных Слотов по контракту Call Calendar API. */
@RestController
@Tag(name = "Slots")
public class SlotController {

  private final SlotService slotService;

  private final EventTypeStore eventTypeStore;

  SlotController(SlotService slotService, EventTypeStore eventTypeStore) {
    this.slotService = slotService;
    this.eventTypeStore = eventTypeStore;
  }

  @Operation(
      operationId = "listSlots",
      description = "Сетка 30-минутных стартов с признаком занятости на запрошенную дату.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "The request has succeeded.",
        useReturnTypeSchema = true),
    @ApiResponse(
        responseCode = "400",
        description = "Некорректные данные запроса.",
        content = @Content(schema = @Schema(implementation = ApiError.class)))
  })
  @GetMapping(value = "/slots", produces = "application/json")
  public List<Slot> list(
      @RequestParam
          @Parameter(
              in = ParameterIn.QUERY,
              description = "Идентификатор типа встречи.",
              required = true,
              explode = Explode.FALSE)
          String eventTypeId,
      @RequestParam
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          @Parameter(
              in = ParameterIn.QUERY,
              description = "Дата, на которую запрашиваются слоты (YYYY-MM-DD).",
              required = true,
              explode = Explode.FALSE)
          LocalDate date) {
    EventType eventType =
        eventTypeStore
            .findById(eventTypeId)
            .orElseThrow(() -> new EventTypeNotFoundException(eventTypeId));
    return slotService.slotsOf(eventType, date);
  }
}
