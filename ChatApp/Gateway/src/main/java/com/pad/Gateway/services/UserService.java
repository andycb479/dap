package com.pad.Gateway.services;

import com.pad.Gateway.dto.user.UpdateStatusDto;
import com.pad.Gateway.dto.user.UserDto;

import java.util.List;

public interface UserService {
  UserDto getUser(int id);

  List<UserDto> getUsers();

  UserDto createUser(UserDto userDto);

  void deleteUser(int id);

  UserDto updateUser(UserDto userDto);

  UserDto updateUserStatus(UpdateStatusDto userDto);
}
