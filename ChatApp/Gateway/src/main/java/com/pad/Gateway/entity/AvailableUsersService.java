package com.pad.Gateway.entity;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import users.*;

import java.util.LinkedList;
import java.util.List;

@Slf4j
public class AvailableUsersService {
  private final String port;

  private final String address;

  private final UsersGrpc.UsersStub stream_stub;

  private final UsersGrpc.UsersBlockingStub stub;

  private static final LinkedList<User> users = new LinkedList<>();

  public AvailableUsersService(String port, String address) {
    this.address = address;
    this.port = port;
    ManagedChannel managedChannel =
        ManagedChannelBuilder.forTarget(address + ":" + port).usePlaintext().build();
    log.info("Adding new available users service with address -> " + address + ":" + port);
    stub = UsersGrpc.newBlockingStub(managedChannel);
    stream_stub = UsersGrpc.newStub(managedChannel).withWaitForReady();
  }

  public User createUserRequest(User request) {
    return stub.createUser(request);
  }

  public User getUserRequest(UserIdRequest request) {
    return stub.getUser(request);
  }

  public User changeUserStatusRequest(UserStatus request) {
    return stub.changeUserStatus(request);
  }

  public User updateUserRequest(User request) {
    return stub.updateUser(request);
  }

  public void deleteUserRequest(UserIdRequest request) {
    GenericReply response = stub.deleteUser(request);
  }

  public List<User> getUsersRequest(UsersRequest request) {
    users.clear();

    stream_stub.getUsers(request, new OutputStreamObserver());

    // TODO improve this
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    return users;
  }

  private static class OutputStreamObserver implements StreamObserver<User> {

    @Override
    public void onNext(User value) {
      log.info("New user with id - " + value.getUserId());
      users.add(value);
    }

    @Override
    public void onError(Throwable t) {
      log.error(t.getMessage());
    }

    @Override
    public void onCompleted() {
      log.info("Users stream completed");
    }
  }

  public String getPort() {
    return port;
  }

  public String getAddress() {
    return address;
  }
}
