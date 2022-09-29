package com.pad.Gateway.entity;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import messages.Message;
import messages.MessagesGrpc;

import java.util.Iterator;

import static messages.MessagesGrpc.newStub;

@Slf4j
public class AvailableService {
  private final String port;

  private final String address;

  private final MessagesGrpc.MessagesStub stub;

  public AvailableService(String port, String address) {
    this.address = address;
    this.port = port;
    ManagedChannel managedChannel =
        ManagedChannelBuilder.forTarget(address + ":" + port).usePlaintext().build();
    stub = newStub(managedChannel);
  }

  public messages.GenericReply sendMessageRequest(messages.SendMessageRequest messageRequest) {
//    messages.GenericReply reply = stub.sendMessage(messageRequest);
//    log.info(reply.getResponse());
//    return reply;
    return null;
  }

  public Iterator<Message> sendChatRequest(messages.ChatRequest chatRequest) {
    Iterator<Message> messageIterator = null;

    stub.getChatMessages(chatRequest, new OutputStreamObserver());

//    try {
//
//
//      stub.cha
//
//      for (int i = 1; messageIterator.hasNext(); i++) {
//        Message message = messageIterator.next();
//        log.info(message.getMessageContent());
//      }
//    } catch (StatusRuntimeException e) {
//      log.error(e.getMessage());
//    }

    return messageIterator;
  }

  public String getPort() {
    return port;
  }

  public String getAddress() {
    return address;
  }

  private static class OutputStreamObserver implements StreamObserver<Message>{

    @Override
    public void onNext(Message value) {
      System.out.println("Received" + value.getMessageContent());
    }

    @Override
    public void onError(Throwable t) {
      System.out.println("Error");
    }

    @Override
    public void onCompleted() {
      System.out.println("Gata");
    }
  }

}
