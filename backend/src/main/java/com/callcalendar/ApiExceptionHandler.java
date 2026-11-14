package com.callcalendar;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/** Единый формат ошибок API: { code, message }. */
@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(EventTypeNotFoundException.class)
  ResponseEntity<ApiError> eventTypeNotFound(EventTypeNotFoundException exception) {
    return badRequest("EVENT_TYPE_NOT_FOUND", exception.getMessage());
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  ResponseEntity<ApiError> invalidQueryParameter(MethodArgumentTypeMismatchException exception) {
    return badRequest(
        "INVALID_QUERY_PARAMETER", "Некорректный параметр запроса: " + exception.getName());
  }

  private static ResponseEntity<ApiError> badRequest(String code, String message) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(code, message));
  }
}
