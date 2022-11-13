package com.pad.Users.service;

import com.pad.Users.dto.UserDto;

import java.util.List;

public interface UserService {
  UserDto getUser(Long id);

  List<UserDto> getUsers();

  UserDto createUser(UserDto userDto);

  boolean deleteUser(Long id);

  UserDto updateUser(UserDto userDto);

  UserDto getUserStatus(Long id);

  UserDto updateUserStatus(UserDto userDto);
}
