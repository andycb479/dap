package com.pad.Users;

import com.pad.Users.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping
    public UserDto addUser(@RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable String userId) {
        return userService.getUser(Long.decode(userId));
    }

    @GetMapping("/all")
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @DeleteMapping("/{userId}")
    public void removeUser(@PathVariable String userId) {
        userService.deleteUser(Long.decode(userId));
    }

}
