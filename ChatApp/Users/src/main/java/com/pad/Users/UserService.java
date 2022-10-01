package com.pad.Users;

import com.pad.Users.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto getUser(Long id);
    List<UserDto> getUsers();
    UserDto createUser(UserDto userDto);
    void deleteUser(Long id);
}
