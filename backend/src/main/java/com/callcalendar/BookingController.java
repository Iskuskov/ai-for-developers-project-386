package com.callcalendar;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Создание Записей Гостями и просмотр предстоящих Записей по контракту Call Calendar API. */
@RestController
@Tag(name = "Bookings")
public class BookingController {

  private final BookingService bookingService;

  private final EventTypeStore eventTypeStore;

  private final BookingOccupancy bookings;

  private final Clock clock;

  BookingController(
      BookingService bookingService,
      EventTypeStore eventTypeStore,
      BookingOccupancy bookings,
      Clock clock) {
    this.bookingService = bookingService;
    this.eventTypeStore = eventTypeStore;
    this.bookings = bookings;
    this.clock = clock;
  }

  @Operation(
      operationId = "listBookings",
      description = "Предстоящие записи всех типов одним списком, по возрастанию времени старта.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "The request has succeeded.",
        useReturnTypeSchema = true)
  })
  @GetMapping(value = "/bookings", produces = "application/json")
  public List<Booking> list() {
    LocalDateTime now = clock.instant().atZone(SlotService.WORK_ZONE).toLocalDateTime();
    return bookings.findUpcoming(now);
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
