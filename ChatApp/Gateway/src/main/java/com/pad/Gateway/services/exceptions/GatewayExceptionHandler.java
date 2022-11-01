package com.pad.Gateway.services.exceptions;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GatewayExceptionHandler {
  @ExceptionHandler(value = StatusRuntimeException.class)
  public ResponseEntity<Object> statusRuntimeException(StatusRuntimeException exception) {
    log.error(exception.getStatus().toString());

    switch (exception.getStatus().getCode()) {
      case DEADLINE_EXCEEDED:
        return new ResponseEntity<>(
            exception.getStatus().getCode() + ": " + exception.getStatus().getDescription(),
            HttpStatus.GATEWAY_TIMEOUT);
      case INVALID_ARGUMENT:
        return new ResponseEntity<>(
            exception.getStatus().getCode() + ": " + exception.getStatus().getDescription(),
            HttpStatus.BAD_REQUEST);
      case UNAVAILABLE:
        return new ResponseEntity<>(
            exception.getStatus().getCode() + ": " + exception.getStatus().getDescription(),
            HttpStatus.SERVICE_UNAVAILABLE);
      default:
        return new ResponseEntity<>(
            exception.getStatus().getCode() + ": " + exception.getStatus().getDescription(),
            HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @ExceptionHandler(value = CallNotPermittedException.class)
  public ResponseEntity<Object> resilienceException(CallNotPermittedException exception) {
    log.error(exception.getMessage());
    return new ResponseEntity<>(exception.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
  }
}
