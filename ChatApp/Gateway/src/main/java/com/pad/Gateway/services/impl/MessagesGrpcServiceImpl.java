package com.pad.Gateway.services.impl;

import io.grpc.stub.StreamObserver;

public class MessagesGrpcServiceImpl extends messages.MessagesGrpc.MessagesImplBase {
  @Override
  public void sendMessage(
      messages.SendMessageRequest request, StreamObserver<messages.GenericReply> responseObserver) {
    System.out.println(request.getMessageContent());
  }

  @Override
  public void getChatMessages(
      messages.ChatRequest request, StreamObserver<messages.Message> responseObserver) {
    System.out.println(request.getChatUserId());
  }

  @Override
  public void changeMessagesForChatToSeen(
      messages.ChatRequest request, StreamObserver<messages.GenericReply> responseObserver) {
    System.out.println(request.getChatUserId());
  }
}
