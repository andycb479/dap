package com.pad.Gateway.controller;

import com.pad.Gateway.dto.UserDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
  @PostMapping
  public UserDto addUser(@RequestBody UserDto userDto) {
    return null;
  }

  @GetMapping("/{userId}")
  public UserDto getUser(@PathVariable String userId) {
    return null;
  }

  @GetMapping("/all")
  public List<UserDto> getUsers() {
    return null;
  }

  @DeleteMapping("/{userId}")
  public void removeUser(@PathVariable String userId) {}
}
