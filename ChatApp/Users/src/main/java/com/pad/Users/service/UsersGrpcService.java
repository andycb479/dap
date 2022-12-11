package com.pad.Users.service;

import com.pad.Users.dto.UserDto;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import users.GenericReply;
import users.User;
import users.UserAndTransactionIdRequest;
import users.UsersRequest;

import java.util.List;

@Service
@Slf4j
public class UsersGrpcService extends users.UsersGrpc.UsersImplBase implements GrpcService {

  @Autowired UserService userService;

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

    if (foundUser == null) {
      log.error("User not found!");
      responseObserver.onError(
          new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription("User not found.")));
    } else {
      users.User.Builder builder = users.User.newBuilder();
      builder.setUserId(foundUser.getId().intValue());
      builder.setFirstName(foundUser.getFirstName());
      builder.setLastName(foundUser.getLastName());
      builder.setStatus(foundUser.getStatus());

      responseObserver.onNext(builder.build());
    }

    responseObserver.onCompleted();
  }

  @Override
  public void getUsers(UsersRequest request, StreamObserver<User> responseObserver) {
    List<UserDto> users = userService.getUsers();

    for (UserDto userDto : users) {
      users.User.Builder builder = User.newBuilder();
      builder.setUserId(userDto.getId().intValue());
      builder.setFirstName(userDto.getFirstName());
      builder.setLastName(userDto.getLastName());
      builder.setStatus(userDto.getStatus());

      responseObserver.onNext(builder.build());
    }

    responseObserver.onCompleted();
  }

  @Override
  public void getUserStatus(
      users.UserIdRequest request, StreamObserver<users.UserStatus> responseObserver) {
    UserDto foundUser = userService.getUserStatus((long) request.getUserId());

    if (foundUser == null) {
      log.error("User not found!");
      responseObserver.onError(
          new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription("User not found.")));
    } else {
      users.UserStatus.Builder builder = users.UserStatus.newBuilder();
      builder.setUserId(foundUser.getId().intValue());
      builder.setStatus(foundUser.getStatus());

      responseObserver.onNext(builder.build());
    }

    responseObserver.onCompleted();
  }

  @Override
  public void changeUserStatus(
      users.UserStatus request, StreamObserver<users.User> responseObserver) {
    UserDto userDto = new UserDto();
    userDto.setStatus(request.getStatus());
    userDto.setId((long) request.getUserId());

    UserDto updatedUser = userService.updateUserStatus(userDto);

    if (updatedUser == null) {
      log.error("User not found!");
      responseObserver.onError(
          new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription("User not found.")));
    } else {
      users.User.Builder builder = users.User.newBuilder();
      builder.setUserId(updatedUser.getId().intValue());
      builder.setFirstName(updatedUser.getFirstName());
      builder.setLastName(updatedUser.getLastName());
      builder.setStatus(updatedUser.getStatus());

      responseObserver.onNext(builder.build());
    }

    responseObserver.onCompleted();
  }

  @Override
  public void updateUser(users.User request, StreamObserver<users.User> responseObserver) {
    UserDto userDto = new UserDto();
    userDto.setStatus(request.getStatus());
    userDto.setId((long) request.getUserId());
    userDto.setFirstName(request.getFirstName());
    userDto.setLastName(request.getLastName());

    UserDto updatedUser = userService.updateUser(userDto);

    if (updatedUser == null) {
      log.error("User not found!");
      responseObserver.onError(
          new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription("User not found.")));
    } else {
      users.User.Builder builder = users.User.newBuilder();
      builder.setUserId(updatedUser.getId().intValue());
      builder.setFirstName(updatedUser.getFirstName());
      builder.setLastName(updatedUser.getLastName());
      builder.setStatus(updatedUser.getStatus());

      responseObserver.onNext(builder.build());
    }

    responseObserver.onCompleted();
  }

  @Override
  public void deleteUser(
      users.UserAndTransactionIdRequest request,
      StreamObserver<users.GenericReply> responseObserver) {
    if (userService.deleteUser((long) request.getUserId(), request.getTransactionId())) {
      users.GenericReply.Builder builder = users.GenericReply.newBuilder();
      builder.setResponse("User deleted!");
      responseObserver.onNext(builder.build());
    } else {
      responseObserver.onError(
          new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription("User not found.")));
    }

    responseObserver.onCompleted();
  }

  @Override
  public void rollbackUserDeletion(
      UserAndTransactionIdRequest request, StreamObserver<GenericReply> responseObserver) {
    if (userService.rollbackUserDeletion((long) request.getUserId(), request.getTransactionId())) {
      users.GenericReply.Builder builder = users.GenericReply.newBuilder();
      builder.setResponse("User deletion rollback executed!");
      responseObserver.onNext(builder.build());
    } else {
      responseObserver.onError(
          new StatusRuntimeException(
              Status.INVALID_ARGUMENT.withDescription("User deletion rollback not executed.")));
    }

    responseObserver.onCompleted();
  }
}
