package com.pad.Gateway.entity;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import messages.Message;
import messages.MessagesGrpc;

import java.util.Iterator;

import static messages.MessagesGrpc.newBlockingStub;

@Slf4j
public class AvailableService {
  private final String port;

  private final MessagesGrpc.MessagesBlockingStub stub;

  public AvailableService(String port) {
    this.port = port;
    ManagedChannel managedChannel =
        ManagedChannelBuilder.forTarget("127.0.0.1:".concat(port)).usePlaintext().build();
    stub = newBlockingStub(managedChannel);
  }

  public messages.GenericReply sendMessageRequest(messages.SendMessageRequest messageRequest) {
    messages.GenericReply reply = stub.sendMessage(messageRequest);
    log.info(reply.getResponse());
    return reply;
  }

  public Iterator<Message> sendChatRequest(messages.ChatRequest chatRequest) {
    return stub.getChatMessages(chatRequest);
  }

  public String getPort() {
    return port;
  }
}
