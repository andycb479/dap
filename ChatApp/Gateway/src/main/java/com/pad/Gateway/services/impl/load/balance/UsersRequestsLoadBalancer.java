package com.pad.Gateway.services.impl.load.balance;

import com.pad.Gateway.dto.user.UserDto;
import com.pad.Gateway.entity.AvailableUsersService;
import com.pad.Gateway.services.AvailableServicesLookup;
import com.pad.Gateway.services.impl.load.balance.distribution.FinalEntityRequestBuilderAndExecutor;
import com.pad.Gateway.services.impl.load.balance.distribution.user.EmptyResUserRequestBuilder;
import com.pad.Gateway.services.impl.load.balance.distribution.user.MultipleUsersResRequestBuilder;
import com.pad.Gateway.services.impl.load.balance.distribution.user.SingleFinalUserResRequestBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import users.User;
import users.UserIdRequest;
import users.UserStatus;
import users.UsersRequest;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

@Service
@Slf4j
public class UsersRequestsLoadBalancer {

  @Autowired AvailableServicesLookup availableServicesLookup;

  private static final AtomicInteger ind = new AtomicInteger(0);

  public static final int MAX_REDISTRIBUTION_TRIES = 10;

  private final FinalEntityRequestBuilderAndExecutor singleUserResRequest =
      new SingleFinalUserResRequestBuilder();
  private final FinalEntityRequestBuilderAndExecutor multipleUsersResRequestBuilder =
      new MultipleUsersResRequestBuilder();
  private final FinalEntityRequestBuilderAndExecutor emptyResUserRequestBuilder =
      new EmptyResUserRequestBuilder();

  private AvailableUsersService getNextAvailableService() {

    List<AvailableUsersService> availableUsersServices =
        availableServicesLookup.getAvailableUsersServices();

    int serviceIndex =
        ind.getAndAccumulate(availableUsersServices.size(), (cur, n) -> cur >= n - 1 ? 0 : cur + 1);
    AvailableUsersService usersService = availableUsersServices.get(serviceIndex);

    log.info(
        "Sending request to users service with address: "
            + usersService.getAddress()
            + ":"
            + usersService.getPort());

    return usersService;
  }

  public UserDto distributeCreateUserRequest(User request) {
    Supplier<Object> userSupplier = () -> getNextAvailableService().createUserRequest(request);
    return (UserDto)
        singleUserResRequest.createAndExecuteRequest(userSupplier, availableServicesLookup);
  }

  public UserDto distributeGetUserRequest(UserIdRequest request) {
    Supplier<Object> userSupplier = () -> getNextAvailableService().getUserRequest(request);
    return (UserDto)
        singleUserResRequest.createAndExecuteRequest(userSupplier, availableServicesLookup);
  }

  @SuppressWarnings("unchecked")
  public List<UserDto> distributeGetUsersRequest(UsersRequest request) {
    Supplier<Object> userSupplier = () -> getNextAvailableService().getUsersRequest(request);
    return (List<UserDto>)
        multipleUsersResRequestBuilder.createAndExecuteRequest(
            userSupplier, availableServicesLookup);
  }

  public void distributeDeleteUserRequest(UserIdRequest request) {
    Supplier<Object> userSupplier = () -> getNextAvailableService().deleteUserRequest(request);
    emptyResUserRequestBuilder.createAndExecuteRequest(userSupplier, availableServicesLookup);
  }

  public UserDto distributeUpdateUserRequest(User request) {
    Supplier<Object> userSupplier = () -> getNextAvailableService().updateUserRequest(request);
    return (UserDto)
        singleUserResRequest.createAndExecuteRequest(userSupplier, availableServicesLookup);
  }

  public UserDto distributeUpdateUserStatusRequest(UserStatus request) {
    Supplier<Object> userSupplier =
        () -> getNextAvailableService().changeUserStatusRequest(request);
    return (UserDto)
        singleUserResRequest.createAndExecuteRequest(userSupplier, availableServicesLookup);
  }
}
