package com.pad.Gateway.services.impl.load.balance.distribution.user;

import com.pad.Gateway.dto.user.UserDto;
import com.pad.Gateway.services.AvailableServicesLookup;
import com.pad.Gateway.services.impl.load.balance.distribution.FinalEntityRequestBuilderAndExecutor;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import users.User;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import static com.pad.Gateway.services.impl.load.balance.UsersRequestsLoadBalancer.MAX_REDISTRIBUTION_TRIES;
import static com.pad.Gateway.services.impl.load.balance.distribution.ExceptionHandlerUtil.handleException;

@Slf4j
public class MultipleUsersResRequestBuilder implements FinalEntityRequestBuilderAndExecutor {
  @Override
  @SuppressWarnings("unchecked")
  public Object createAndExecuteRequest(
      Supplier<Object> userSupplier, AvailableServicesLookup availableServicesLookup) {
    int servicesTriedCount = 0;
    int servicesToTryCount = availableServicesLookup.getAvailableUsersServices().size();

    while (servicesTriedCount < servicesToTryCount
        && servicesTriedCount <= MAX_REDISTRIBUTION_TRIES) {
      try {
        Iterator<User> users = (Iterator<User>) userSupplier.get();
        List<UserDto> usersToReturn = new LinkedList<>();

        users.forEachRemaining(
            user -> {
              UserDto userDto = new UserDto();
              BeanUtils.copyProperties(user, userDto);
              usersToReturn.add(userDto);
            });

        return usersToReturn;
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
