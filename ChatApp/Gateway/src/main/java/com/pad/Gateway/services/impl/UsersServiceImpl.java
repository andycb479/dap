package com.pad.Gateway.services.impl;

import com.pad.Gateway.dto.user.UpdateStatusDto;
import com.pad.Gateway.dto.user.UserDto;
import com.pad.Gateway.services.UserService;
import com.pad.Gateway.services.impl.load.balance.UsersRequestsLoadBalancer;
import lombok.extern.slf4j.Slf4j;
import orchestrator.DeleteUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import users.User;
import users.UserIdRequest;
import users.UserStatus;
import users.UsersRequest;

import java.util.List;
import java.util.function.Supplier;

import static com.pad.Gateway.services.util.CircuitBreakerContext.getUserServiceCB;

@Service
@Slf4j
public class UsersServiceImpl implements UserService {

  @Autowired UsersRequestsLoadBalancer loadBalancer;

  @Override
  public UserDto getUser(int id) {
    UserIdRequest request = UserIdRequest.newBuilder().setUserId(id).build();

    Supplier<UserDto> messageSupplier = () -> loadBalancer.distributeGetUserRequest(request);
    Supplier<UserDto> decoratedMessageSupplier =
        getUserServiceCB().decorateSupplier(messageSupplier);

    return decoratedMessageSupplier.get();
  }

  @Override
  public List<UserDto> getUsers() {
    UsersRequest request = UsersRequest.newBuilder().build();

    Supplier<List<UserDto>> messageSupplier = () -> loadBalancer.distributeGetUsersRequest(request);
    Supplier<List<UserDto>> decoratedMessageSupplier =
        getUserServiceCB().decorateSupplier(messageSupplier);

    return decoratedMessageSupplier.get();
  }

  @Override
  public UserDto createUser(UserDto userDto) {
    User request =
        User.newBuilder()
            .setFirstName(userDto.getFirstName())
            .setLastName(userDto.getLastName())
            .setStatus(userDto.getStatus())
            .build();

    Supplier<UserDto> messageSupplier = () -> loadBalancer.distributeCreateUserRequest(request);
    Supplier<UserDto> decoratedMessageSupplier =
        getUserServiceCB().decorateSupplier(messageSupplier);

    return decoratedMessageSupplier.get();
  }

  @Override
  public void deleteUser(int id) {
    DeleteUserRequest request = DeleteUserRequest.newBuilder().setUserId(id).build();
    Runnable runnable = () -> loadBalancer.distributeDeleteUserRequestSaga(request);
    getUserServiceCB().decorateRunnable(runnable).run();
  }

  @Override
  public UserDto updateUser(UserDto userDto) {
    User request =
        User.newBuilder()
            .setUserId(userDto.getUserId())
            .setFirstName(userDto.getFirstName())
            .setLastName(userDto.getLastName())
            .setStatus(userDto.getStatus())
            .build();

    Supplier<UserDto> messageSupplier = () -> loadBalancer.distributeUpdateUserRequest(request);
    Supplier<UserDto> decoratedMessageSupplier =
        getUserServiceCB().decorateSupplier(messageSupplier);

    return decoratedMessageSupplier.get();
  }

  @Override
  public UserDto updateUserStatus(UpdateStatusDto userDto) {
    UserStatus request =
        UserStatus.newBuilder()
            .setUserId(userDto.getUserId())
            .setStatus(userDto.getStatus())
            .build();

    Supplier<UserDto> messageSupplier =
        () -> loadBalancer.distributeUpdateUserStatusRequest(request);
    Supplier<UserDto> decoratedMessageSupplier =
        getUserServiceCB().decorateSupplier(messageSupplier);

    return decoratedMessageSupplier.get();
  }
}
