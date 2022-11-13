package com.pad.Gateway.entity;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import messages.Message;
import messages.MessagesGrpc;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import static messages.MessagesGrpc.newBlockingStub;

@Slf4j
public class AvailableChatService {
  private final String port;

  private final String address;

  private final MessagesGrpc.MessagesBlockingStub stream_stub;

  private final MessagesGrpc.MessagesBlockingStub stub;

  public AvailableChatService(String port, String address) {
    this.address = address;
    this.port = port;
    ManagedChannel managedChannel =
        ManagedChannelBuilder.forTarget(address + ":" + port).usePlaintext().build();
    log.info("Adding new available chat service with address -> " + address + ":" + port);
    stub = newBlockingStub(managedChannel);
    stream_stub = newBlockingStub(managedChannel).withWaitForReady();
  }

  public messages.GenericReply sendMessageRequest(messages.SendMessageRequest messageRequest) {
    messages.GenericReply reply = stub.sendMessage(messageRequest);
    log.info(reply.getResponse());
    return reply;
  }

  public Iterator<Message> sendChatRequest(messages.ChatRequest chatRequest) {
    return stream_stub.withDeadlineAfter(6, TimeUnit.SECONDS).getChatMessages(chatRequest);
  }

  public String getPort() {
    return port;
  }

  public String getAddress() {
    return address;
  }
}
