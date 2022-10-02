package com.pad.Users.service;

import com.pad.Users.UserService;
import com.pad.Users.dto.UserDto;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class UsersGrpcService extends users.UsersGrpc.UsersImplBase {

    @Autowired
    UserService userService;

    @Override
    public void createUser(users.User request, StreamObserver<users.User> responseObserver) {
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(request, userDto);

        UserDto createdUser = userService.createUser(userDto);

        users.User.Builder builder = users.User.newBuilder();
        builder.setUserId(createdUser.getId().intValue());
        builder.setFirstName(createdUser.getFirstName());
        builder.setLastName(createdUser.getLastName());
        builder.setStatus(createdUser.getStatus());

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getUser(users.UserIdRequest request, StreamObserver<users.User> responseObserver) {
        UserDto foundUser = userService.getUser((long) request.getUserId());

        users.User.Builder builder = users.User.newBuilder();
        builder.setUserId(foundUser.getId().intValue());
        builder.setFirstName(foundUser.getFirstName());
        builder.setLastName(foundUser.getLastName());
        builder.setStatus(foundUser.getStatus());

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getUserStatus(users.UserIdRequest request, StreamObserver<users.UserStatus> responseObserver) {
        UserDto foundUser = userService.getUser((long) request.getUserId());

        users.UserStatus.Builder builder = users.UserStatus.newBuilder();
        builder.setUserId(foundUser.getId().intValue());
        builder.setStatus(foundUser.getStatus());

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void changeUserStatus(users.UserStatus request, StreamObserver<users.User> responseObserver) {
        UserDto userDto = new UserDto();
        userDto.setStatus(request.getStatus());
        userDto.setId( (long) request.getUserId());

        UserDto updatedUser = userService.updateUser(userDto);

        users.User.Builder builder = users.User.newBuilder();
        builder.setUserId(updatedUser.getId().intValue());
        builder.setFirstName(updatedUser.getFirstName());
        builder.setLastName(updatedUser.getLastName());
        builder.setStatus(updatedUser.getStatus());

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void updateUser(users.User request, StreamObserver<users.User> responseObserver) {
        UserDto userDto = new UserDto();
        userDto.setStatus(request.getStatus());
        userDto.setId( (long) request.getUserId());
        userDto.setFirstName(request.getFirstName());
        userDto.setFirstName(request.getLastName());

        UserDto updatedUser = userService.updateUser(userDto);

        users.User.Builder builder = users.User.newBuilder();
        builder.setUserId(updatedUser.getId().intValue());
        builder.setFirstName(updatedUser.getFirstName());
        builder.setLastName(updatedUser.getLastName());
        builder.setStatus(updatedUser.getStatus());

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void deleteUser(users.UserIdRequest request, StreamObserver<users.GenericReply> responseObserver) {
        userService.deleteUser( (long) request.getUserId());

        users.GenericReply.Builder builder = users.GenericReply.newBuilder();
        builder.setResponse("User deleted!");

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }
}
