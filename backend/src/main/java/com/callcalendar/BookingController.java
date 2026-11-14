package com.callcalendar;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Создание Записей Гостями по контракту Call Calendar API. */
@RestController
@Tag(name = "Bookings")
public class BookingController {

  private final BookingService bookingService;

  private final EventTypeStore eventTypeStore;

  BookingController(BookingService bookingService, EventTypeStore eventTypeStore) {
    this.bookingService = bookingService;
    this.eventTypeStore = eventTypeStore;
  }

  @Operation(
      operationId = "createBooking",
      description = "Создание записи гостем на свободный слот.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Созданная запись.",
        useReturnTypeSchema = true),
    @ApiResponse(
        responseCode = "400",
        description = "Некорректные данные запроса.",
        content = @Content(schema = @Schema(implementation = ApiError.class))),
    @ApiResponse(
        responseCode = "409",
        description = "Время уже занято другой записью.",
        content = @Content(schema = @Schema(implementation = ApiError.class)))
  })
  @PostMapping(value = "/bookings", consumes = "application/json", produces = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public Booking create(@Valid @RequestBody BookingInput input) {
    EventType eventType =
        eventTypeStore.findAll().stream()
            .filter(type -> type.id().equals(input.eventTypeId()))
            .findFirst()
            .orElseThrow(() -> new EventTypeNotFoundException(input.eventTypeId()));
    return bookingService.create(eventType, input);
  }
}
