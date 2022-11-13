package com.pad.Gateway.services.impl;

import com.pad.Gateway.dto.message.ChatDto;
import com.pad.Gateway.dto.message.MessageDto;
import com.pad.Gateway.dto.message.SendMessageDto;
import com.pad.Gateway.services.MessagesService;
import com.pad.Gateway.services.impl.load.balance.MessagesRequestsLoadBalancer;
import messages.GenericReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.function.Supplier;

import static com.pad.Gateway.services.util.CircuitBreakerContext.getChatServiceCB;

@Service
public class MessagesServicesImpl implements MessagesService {

  @Autowired MessagesRequestsLoadBalancer loadBalancer;

  @Override
  public GenericReply sendMessage(SendMessageDto messageDto) {
    messages.SendMessageRequest request =
        messages.SendMessageRequest.newBuilder()
            .setMessageContent(messageDto.getMessageContent())
            .setFromUserId(messageDto.getFromUserId())
            .setToUserId(messageDto.getToUserId())
            .build();

    Supplier<GenericReply> messageSupplier = () -> loadBalancer.distributeMessageRequest(request);
    Supplier<GenericReply> decoratedMessageSupplier =
        getChatServiceCB().decorateSupplier(messageSupplier);

    return decoratedMessageSupplier.get();
  }

  @Override
  public LinkedList<MessageDto> getChat(ChatDto chatDto) {
    messages.ChatRequest request =
        messages.ChatRequest.newBuilder()
            .setRequestUserId(chatDto.getRequestUserId())
            .setChatUserId(chatDto.getChatUserId())
            .build();

    Supplier<LinkedList<MessageDto>> messageSupplier =
        () -> loadBalancer.distributeChatRequest(request);
    Supplier<LinkedList<MessageDto>> decoratedMessageSupplier =
        getChatServiceCB().decorateSupplier(messageSupplier);

    return decoratedMessageSupplier.get();
  }

  @Override
  public void changeStatusToSeen(ChatDto chatDto) {
    messages.ChatRequest request =
        messages.ChatRequest.newBuilder()
            .setRequestUserId(chatDto.getRequestUserId())
            .setChatUserId(chatDto.getChatUserId())
            .build();

    Supplier<LinkedList<MessageDto>> messageSupplier =
        () -> loadBalancer.distributeChatRequest(request);
    Supplier<LinkedList<MessageDto>> decoratedMessageSupplier =
        getChatServiceCB().decorateSupplier(messageSupplier);

    decoratedMessageSupplier.get();
  }
}
