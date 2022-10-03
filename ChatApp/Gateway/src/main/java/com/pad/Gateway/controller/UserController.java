package com.pad.Gateway.controller;

import com.pad.Gateway.dto.user.UpdateStatusDto;
import com.pad.Gateway.dto.user.UserDto;
import com.pad.Gateway.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

  @Autowired UserService userService;

  @PostMapping
  public UserDto addUser(@RequestBody UserDto userDto) {
    return userService.createUser(userDto);
  }

  @GetMapping("/{userId}")
  public UserDto getUser(@PathVariable String userId) {
    return userService.getUser(Integer.parseInt(userId));
  }

  @PutMapping
  public UserDto updateUserStatus(@RequestBody UpdateStatusDto userDto) {
    return userService.updateUserStatus(userDto);
  }

  @GetMapping
  public List<UserDto> getUsers() {
    return userService.getUsers();
  }

  @PutMapping
  public UserDto updateUser(@RequestBody UserDto userDto) {
    return userService.updateUser(userDto);
  }

  @DeleteMapping("/{userId}")
  public void removeUser(@PathVariable String userId) {
    userService.deleteUser(Integer.parseInt(userId));
  }
}
