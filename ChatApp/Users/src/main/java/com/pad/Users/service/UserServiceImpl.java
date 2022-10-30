package com.pad.Users.service;

import com.pad.Users.UserRepository;
import com.pad.Users.dto.UserDto;
import com.pad.Users.entity.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
  @Autowired
  UserRepository userRepository;

  @Override
  public UserDto getUser(Long id) {
    Optional<User> user = userRepository.findById(id);

    if (user.isPresent()) {
      UserDto userDto = new UserDto();
      BeanUtils.copyProperties(user.get(), userDto);
      return userDto;
    } else {
      return null;
    }
  }

  @Override
  public List<UserDto> getUsers() {
    List<User> users = userRepository.findAll();
    List<UserDto> userToReturn = new LinkedList<>();

    users.forEach(
        user -> {
          UserDto userDto = new UserDto();
          BeanUtils.copyProperties(user, userDto);
          userToReturn.add(userDto);
        });

    return userToReturn;
  }

  @Override
  public UserDto createUser(UserDto userDto) {
    User user = new User();
    BeanUtils.copyProperties(userDto, user);

    User savedUser = userRepository.save(user);
    UserDto userToReturn = new UserDto();

    BeanUtils.copyProperties(savedUser, userToReturn);

    return userToReturn;
  }

  @Override
  public UserDto updateUser(UserDto userDto) {
    Optional<User> user = userRepository.findById(userDto.getId());

    if (!user.isPresent()) {
      return null;
    }

    User existingUser = user.get();

    existingUser.setFirstName(userDto.getFirstName());
    existingUser.setLastName(userDto.getLastName());
    existingUser.setStatus(userDto.getStatus());

    User updatedUser = userRepository.save(existingUser);
    UserDto userToReturn = new UserDto();

    BeanUtils.copyProperties(updatedUser, userToReturn);

    return userToReturn;
  }

  @Override
  public UserDto getUserStatus(Long id) {
    Optional<User> user = userRepository.findById(id);

    if (user.isPresent()) {
      UserDto userDto = new UserDto();
      BeanUtils.copyProperties(user.get(), userDto);
      return userDto;
    } else {
      return null;
    }
  }

  @Override
  public UserDto updateUserStatus(UserDto userDto) {
    Optional<User> existingUser = userRepository.findById(userDto.getId());

    if (!existingUser.isPresent()) {
      return null;
    }

    existingUser.get().setStatus(userDto.getStatus());

    User updatedUser = userRepository.save(existingUser.get());
    UserDto userToReturn = new UserDto();

    BeanUtils.copyProperties(updatedUser, userToReturn);

    return userToReturn;
  }

  @Override
  public boolean deleteUser(Long id) {
    Optional<User> existingUser = userRepository.findById(id);

    if (!existingUser.isPresent()) {
      return false;
    }

    userRepository.deleteById(id);
    return true;
  }
}
