package com.pad.Gateway.entity;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import users.*;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AvailableUsersService {
  private final String port;

  private final String address;

  private final UsersGrpc.UsersBlockingStub stream_stub;

  private final UsersGrpc.UsersBlockingStub stub;

  public AvailableUsersService(String port, String address) {
    this.address = address;
    this.port = port;
    ManagedChannel managedChannel =
        ManagedChannelBuilder.forTarget(address + ":" + port).usePlaintext().build();
    log.info("Adding new available users service with address -> " + address + ":" + port);
    stub = UsersGrpc.newBlockingStub(managedChannel);
    stream_stub = UsersGrpc.newBlockingStub(managedChannel).withWaitForReady();
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
    log.error(response.getResponse());
  }

  public Iterator<User> getUsersRequest(UsersRequest request) {
    return stream_stub.withDeadlineAfter(6, TimeUnit.SECONDS).getUsers(request);
  }

  public String getPort() {
    return port;
  }

  public String getAddress() {
    return address;
  }
}
