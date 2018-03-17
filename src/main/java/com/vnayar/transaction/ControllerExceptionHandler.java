package com.vnayar.transaction;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Convert exceptions into the appropriate controller response.
 */
@ControllerAdvice
class ControllerExceptionHandler {
  @ResponseStatus(HttpStatus.NO_CONTENT)  // 204
  @ExceptionHandler(IdRangeException.class)
  public void handleConflict() {
    // Nothing to do
  }
}