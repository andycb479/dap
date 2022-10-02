package com.pad.Users;

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
            return new UserDto();
        }
    }

    @Override
    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> userToReturn = new LinkedList<>();

        users.forEach(user -> {
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
        User existingUser = userRepository.findById(userDto.getId()).get();
        existingUser.setFirstName(userDto.getFirstName());
        existingUser.setLastName(userDto.getLastName());
        existingUser.setStatus(userDto.getStatus());

        User updatedUser = userRepository.save(existingUser);
        UserDto userToReturn = new UserDto();

        BeanUtils.copyProperties(updatedUser, userToReturn);

        return userToReturn;
    }

    @Override
    public UserDto updateUserStatus(UserDto userDto) {
        User existingUser = userRepository.findById(userDto.getId()).get();

        existingUser.setStatus(userDto.getStatus());

        User updatedUser = userRepository.save(existingUser);
        UserDto userToReturn = new UserDto();

        BeanUtils.copyProperties(updatedUser, userToReturn);

        return userToReturn;
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
