package com.pad.Gateway.services.impl;

import com.pad.Gateway.dto.message.ChatDto;
import com.pad.Gateway.dto.message.MessageDto;
import com.pad.Gateway.dto.message.SendMessageDto;
import com.pad.Gateway.services.MessagesService;
import com.pad.Gateway.services.impl.load.balance.MessagesRequestsLoadBalancer;
import messages.GenericReply;
import messages.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

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

    return loadBalancer.distributeMessageRequest(request);
  }

  @Override
  public LinkedList<MessageDto> getChat(ChatDto chatDto) {
    messages.ChatRequest request =
        messages.ChatRequest.newBuilder()
            .setRequestUserId(chatDto.getRequestUserId())
            .setChatUserId(chatDto.getChatUserId())
            .build();

    LinkedList<Message> chatList = loadBalancer.distributeChatRequest(request);
    LinkedList<MessageDto> messageDtos = new LinkedList<>();
    chatList.forEach(
        chat ->
            messageDtos.add(
                new MessageDto(
                    chat.getMessageStatus(),
                    chat.getFromUserId(),
                    chat.getToUserId(),
                    chat.getDate(),
                    chat.getMessageContent())));

    return messageDtos;
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
