package com.pad.Gateway.services.impl;

import com.pad.Gateway.dto.ChatDto;
import com.pad.Gateway.dto.SendMessageDto;
import com.pad.Gateway.services.MessagesService;
import messages.GenericReply;
import messages.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.LinkedList;

@Service
public class MessagesServicesImpl implements MessagesService {

  @Autowired LoadBalancer loadBalancer;

  @Override
  public GenericReply sendMessage(SendMessageDto messageDto) {
    messages.SendMessageRequest request =
        messages.SendMessageRequest.newBuilder()
            .setMessageContent(messageDto.getMessageContent())
            .setFromUserId(messageDto.getFromUserId())
            .setToUserId(messageDto.getToUserId())
            .build();

    return loadBalancer.distributeMessageRequest(request);
  }

  @Override
  public LinkedList<Message> getChat(ChatDto chatDto) {
    messages.ChatRequest request =
        messages.ChatRequest.newBuilder()
            .setRequestUserId(chatDto.getRequestUserId())
            .setChatUserId(chatDto.getChatUserId())
            .build();

    Iterator<Message> messages = loadBalancer.distributeChatRequest(request);
    LinkedList<Message> chatList = new LinkedList<>();

    messages.forEachRemaining(chatList::add);

    return chatList;
  }

  @Override
  public void changeStatusToSeen(ChatDto chatDto) {
    messages.ChatRequest request =
        messages.ChatRequest.newBuilder()
            .setRequestUserId(chatDto.getRequestUserId())
            .setChatUserId(chatDto.getChatUserId())
            .build();

    loadBalancer.distributeChatRequest(request);
  }
}
