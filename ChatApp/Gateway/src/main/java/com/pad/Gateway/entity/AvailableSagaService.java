package com.pad.Gateway.entity;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import orchestrator.DeleteUserRequest;
import orchestrator.GenericReply;
import orchestrator.OrchestratorGrpc;

@Slf4j
public class AvailableSagaService {
  private final String port;

  private final String address;

  private final OrchestratorGrpc.OrchestratorBlockingStub stream_stub;

  private final OrchestratorGrpc.OrchestratorBlockingStub stub;

  public AvailableSagaService(String port, String address) {
    this.address = address;
    this.port = port;
    ManagedChannel managedChannel =
        ManagedChannelBuilder.forTarget(address + ":" + port).usePlaintext().build();
    log.info("Adding new available saga service with address -> " + address + ":" + port);
    stub = OrchestratorGrpc.newBlockingStub(managedChannel);
    stream_stub = OrchestratorGrpc.newBlockingStub(managedChannel).withWaitForReady();
  }

  public String deleteUserRequest(DeleteUserRequest request) throws StatusRuntimeException {
    GenericReply response = stub.deleteUser(request);
    return response.getResponse();
  }

  public String getPort() {
    return port;
  }

  public String getAddress() {
    return address;
  }
}
