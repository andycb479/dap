package com.pad.Gateway.entity;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import messages.Message;
import messages.MessagesGrpc;

import java.util.LinkedList;

import static messages.MessagesGrpc.newBlockingStub;
import static messages.MessagesGrpc.newStub;

@Slf4j
public class AvailableChatService {
  private final String port;

  private final String address;

  private final MessagesGrpc.MessagesStub stream_stub;

  private final MessagesGrpc.MessagesBlockingStub stub;

  private static final LinkedList<Message> messages = new LinkedList<>();

  public AvailableChatService(String port, String address) {
    this.address = address;
    this.port = port;
    ManagedChannel managedChannel =
        ManagedChannelBuilder.forTarget(address + ":" + port).usePlaintext().build();
    log.info("Adding new available chat service with address -> " + address + ":" + port);
    stub = newBlockingStub(managedChannel);
    stream_stub = newStub(managedChannel).withWaitForReady();
  }

  public messages.GenericReply sendMessageRequest(messages.SendMessageRequest messageRequest) {
    messages.GenericReply reply = stub.sendMessage(messageRequest);
    log.info(reply.getResponse());
    return reply;
  }

  public LinkedList<Message> sendChatRequest(messages.ChatRequest chatRequest) {
    messages.clear();

    stream_stub.getChatMessages(chatRequest, new OutputStreamObserver());

    //TODO improve this
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    return messages;
  }

  private static class OutputStreamObserver implements StreamObserver<Message> {

    @Override
    public void onNext(Message value) {
      messages.add(value);
      log.info("New message: " + value.getMessageContent());
    }

    @Override
    public void onError(Throwable t) {
      log.error(t.getMessage());
    }

    @Override
    public void onCompleted() {
      log.info("Stream completed");
    }
  }
}
