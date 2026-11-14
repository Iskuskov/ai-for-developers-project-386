package com.callcalendar;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/** Единый формат ошибок API { code, message }: некорректные данные — 400. */
@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiError onValidationError(MethodArgumentNotValidException exception) {
    String message =
        exception.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(FieldError::getDefaultMessage)
            .orElse("Некорректные данные запроса.");
    return new ApiError("VALIDATION_FAILED", message);
  }

  @ExceptionHandler(EventTypeNotFoundException.class)
  public ResponseEntity<ApiError> eventTypeNotFound(EventTypeNotFoundException exception) {
    return badRequest("EVENT_TYPE_NOT_FOUND", exception.getMessage());
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiError> invalidQueryParameter(
      MethodArgumentTypeMismatchException exception) {
    return badRequest(
        "INVALID_QUERY_PARAMETER", "Некорректный параметр запроса: " + exception.getName());
  }

  private static ResponseEntity<ApiError> badRequest(String code, String message) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(code, message));
  }
}
