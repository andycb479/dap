package com.pad.Gateway.services.impl.load.balance.distribution.user;

import com.pad.Gateway.services.AvailableServicesLookup;
import com.pad.Gateway.services.impl.load.balance.distribution.FinalEntityRequestBuilderAndExecutor;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

import static com.pad.Gateway.services.impl.load.balance.UsersRequestsLoadBalancer.MAX_REDISTRIBUTION_TRIES;
import static com.pad.Gateway.services.impl.load.balance.distribution.ExceptionHandlerUtil.handleException;

@Slf4j
public class EmptyResUserRequestBuilder implements FinalEntityRequestBuilderAndExecutor {
  @Override
  public Object createAndExecuteRequest(
      Supplier<Object> actionSupplier, AvailableServicesLookup availableServicesLookup) {
    int servicesTriedCount = 0;
    int servicesToTryCount = availableServicesLookup.getAvailableUsersServices().size();

    while (servicesTriedCount < servicesToTryCount
        && servicesTriedCount <= MAX_REDISTRIBUTION_TRIES) {
      try {
        return actionSupplier.get();
      } catch (StatusRuntimeException exception) {
        servicesTriedCount++;
        servicesToTryCount = availableServicesLookup.getAvailableUsersServices().size();
        handleException(exception);
      }
    }

    log.warn("No available instances found!");

    throw new StatusRuntimeException(Status.UNAVAILABLE.withDescription("No instances available!"));
  }
}
