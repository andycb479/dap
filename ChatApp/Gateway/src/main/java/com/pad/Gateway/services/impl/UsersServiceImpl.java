package com.pad.Gateway.services.impl;

import com.pad.Gateway.dto.user.UpdateStatusDto;
import com.pad.Gateway.dto.user.UserDto;
import com.pad.Gateway.services.UserService;
import com.pad.Gateway.services.impl.load.balance.UsersRequestsLoadBalancer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import users.User;
import users.UserIdRequest;
import users.UserStatus;
import users.UsersRequest;

import java.util.LinkedList;
import java.util.List;

@Service
public class UsersServiceImpl implements UserService {

  @Autowired UsersRequestsLoadBalancer loadBalancer;

  @Override
  public UserDto getUser(int id) {
    UserIdRequest request = UserIdRequest.newBuilder().setUserId(id).build();

    User user = loadBalancer.getNextAvailableService().getUserRequest(request);
    UserDto userToReturn = new UserDto();

    BeanUtils.copyProperties(user, userToReturn);

    return userToReturn;
  }

  @Override
  public List<UserDto> getUsers() {
    UsersRequest request = UsersRequest.newBuilder().build();

    List<User> users = loadBalancer.getNextAvailableService().getUsersRequest(request);
    List<UserDto> usersToReturn = new LinkedList<>();

    users.forEach(
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

    User createdUser = loadBalancer.getNextAvailableService().createUserRequest(request);
    UserDto userToReturn = new UserDto();

    BeanUtils.copyProperties(createdUser, userToReturn);

    return userToReturn;
  }

  @Override
  public void deleteUser(int id) {
    UserIdRequest request = UserIdRequest.newBuilder().setUserId(id).build();
    loadBalancer.getNextAvailableService().deleteUserRequest(request);
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

    User updatedUser = loadBalancer.getNextAvailableService().updateUserRequest(request);
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

    User userWithUpdatedStatus =
        loadBalancer.getNextAvailableService().changeUserStatusRequest(request);
    UserDto userToReturn = new UserDto();

    BeanUtils.copyProperties(userWithUpdatedStatus, userToReturn);

    return userToReturn;
  }
}
