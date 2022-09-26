package com.pad.Gateway.services;

import com.pad.Gateway.grpc.*;
import io.grpc.stub.StreamObserver;

public class MessagesServiceImpl extends MessagesGrpc.MessagesImplBase {
  @Override
  public void sendMessage(
      SendMessageRequest request, StreamObserver<GenericReply> responseObserver) {
    System.out.println(request.getMessageContent());
  }

  @Override
  public void getChatMessages(ChatRequest request, StreamObserver<Message> responseObserver) {
    System.out.println(request.getChatUserId());
  }

  @Override
  public void changeMessagesForChatToSeen(
      ChatRequest request, StreamObserver<GenericReply> responseObserver) {
    System.out.println(request.getChatUserId());
  }
}
