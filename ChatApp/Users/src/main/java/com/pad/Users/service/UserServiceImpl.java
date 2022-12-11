package com.pad.Users.service;

import com.pad.Users.UserRepository;
import com.pad.Users.dto.UserDto;
import com.pad.Users.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
  @Autowired UserRepository userRepository;

  @Override
  public UserDto getUser(Long id) {
    Optional<User> user = userRepository.findById(id);

    if (user.isPresent() && user.get().isActive()) {
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
          if (user.isActive()) {
            BeanUtils.copyProperties(user, userDto);
            userToReturn.add(userDto);
          }
        });

    return userToReturn;
  }

  @Override
  public UserDto createUser(UserDto userDto) {
    User user = new User();
    BeanUtils.copyProperties(userDto, user);

    user.setActive(true);

    User savedUser = userRepository.save(user);
    UserDto userToReturn = new UserDto();

    BeanUtils.copyProperties(savedUser, userToReturn);

    return userToReturn;
  }

  @Override
  public UserDto updateUser(UserDto userDto) {
    Optional<User> user = userRepository.findById(userDto.getId());

    if (!user.isPresent() || !user.get().isActive()) {
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

    if (user.isPresent() && user.get().isActive()) {
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

    if (!existingUser.isPresent() || !existingUser.get().isActive()) {
      return null;
    }

    existingUser.get().setStatus(userDto.getStatus());

    User updatedUser = userRepository.save(existingUser.get());
    UserDto userToReturn = new UserDto();

    BeanUtils.copyProperties(updatedUser, userToReturn);

    return userToReturn;
  }

  @Override
  public boolean deleteUser(Long id, String transactionId) {
    Optional<User> existingUser = userRepository.findById(id);

    if (!existingUser.isPresent() || !existingUser.get().isActive()) {
      return false;
    }

    User user = existingUser.get();

    user.setActive(false);
    user.setLastTransactionId(transactionId);

    userRepository.save(user);

    log.info("User with id " + id + " set to inactive.");

    return true;
  }

  @Override
  public boolean rollbackUserDeletion(Long id, String transactionId) {
    Optional<User> existingUser = userRepository.findById(id);

    if (!existingUser.isPresent() || existingUser.get().isActive()) {
      return false;
    }

    User user = existingUser.get();

    if (!user.getLastTransactionId().equals(transactionId)) {
      log.warn("Last transaction ID not equal to current transaction ID. Aborting the rollback.");
      return false;
    }

    user.setActive(true);
    user.setLastTransactionId(null);

    userRepository.save(user);

    return true;
  }
}
