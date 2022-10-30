package com.pad.Gateway.services.impl;

import com.pad.Gateway.dto.user.UpdateStatusDto;
import com.pad.Gateway.dto.user.UserDto;
import com.pad.Gateway.services.UserService;
import com.pad.Gateway.services.impl.load.balance.UsersRequestsLoadBalancer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import users.User;
import users.UserIdRequest;
import users.UserStatus;
import users.UsersRequest;

import java.util.Iterator;
import java.util.LinkedList;
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

    Supplier<User> messageSupplier =
        () -> loadBalancer.getNextAvailableService().getUserRequest(request);
    Supplier<User> decoratedMessageSupplier = getUserServiceCB().decorateSupplier(messageSupplier);
    User user = decoratedMessageSupplier.get();

    UserDto userToReturn = new UserDto();
    BeanUtils.copyProperties(user, userToReturn);

    return userToReturn;
  }

  @Override
  public List<UserDto> getUsers() {
    UsersRequest request = UsersRequest.newBuilder().build();

    Supplier<Iterator<User>> messageSupplier =
        () -> loadBalancer.getNextAvailableService().getUsersRequest(request);
    Supplier<Iterator<User>> decoratedMessageSupplier =
        getUserServiceCB().decorateSupplier(messageSupplier);
    Iterator<User> users = decoratedMessageSupplier.get();

    List<UserDto> usersToReturn = new LinkedList<>();

    users.forEachRemaining(
        user -> {
          UserDto userDto = new UserDto();
          BeanUtils.copyProperties(user, userDto);
          usersToReturn.add(userDto);
        });

    return usersToReturn;
  }

  @Override
  public UserDto createUser(UserDto userDto) {
    User request =
        User.newBuilder()
            .setFirstName(userDto.getFirstName())
            .setLastName(userDto.getLastName())
            .setStatus(userDto.getStatus())
            .build();

    Supplier<User> messageSupplier =
        () -> loadBalancer.getNextAvailableService().createUserRequest(request);
    Supplier<User> decoratedMessageSupplier = getUserServiceCB().decorateSupplier(messageSupplier);
    User createdUser = decoratedMessageSupplier.get();

    UserDto userToReturn = new UserDto();
    BeanUtils.copyProperties(createdUser, userToReturn);

    return userToReturn;
  }

  @Override
  public void deleteUser(int id) {
    UserIdRequest request = UserIdRequest.newBuilder().setUserId(id).build();
    Runnable runnable = () -> loadBalancer.getNextAvailableService().deleteUserRequest(request);
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

    Supplier<User> messageSupplier =
        () -> loadBalancer.getNextAvailableService().updateUserRequest(request);
    Supplier<User> decoratedMessageSupplier = getUserServiceCB().decorateSupplier(messageSupplier);
    User updatedUser = decoratedMessageSupplier.get();

    UserDto userToReturn = new UserDto();
    BeanUtils.copyProperties(updatedUser, userToReturn);

    return userToReturn;
  }

  @Override
  public UserDto updateUserStatus(UpdateStatusDto userDto) {
    UserStatus request =
        UserStatus.newBuilder()
            .setUserId(userDto.getUserId())
            .setStatus(userDto.getStatus())
            .build();

    Supplier<User> messageSupplier =
        () -> loadBalancer.getNextAvailableService().changeUserStatusRequest(request);
    Supplier<User> decoratedMessageSupplier = getUserServiceCB().decorateSupplier(messageSupplier);
    User userWithUpdatedStatus = decoratedMessageSupplier.get();

    UserDto userToReturn = new UserDto();
    BeanUtils.copyProperties(userWithUpdatedStatus, userToReturn);

    return userToReturn;
  }
}
