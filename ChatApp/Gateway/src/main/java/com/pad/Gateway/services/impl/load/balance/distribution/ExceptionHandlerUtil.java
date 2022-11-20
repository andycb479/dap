package com.pad.Gateway.services.impl.load.balance.distribution;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExceptionHandlerUtil {
  public static void handleException(StatusRuntimeException exception) {
    Status.Code code = exception.getStatus().getCode();
    if (code.equals(Status.Code.UNAVAILABLE) || code.equals(Status.Code.DEADLINE_EXCEEDED)) {
      log.error(exception.getMessage());
      log.warn("Trying another instance...");
    } else {
      throw exception;
    }
  }
}
