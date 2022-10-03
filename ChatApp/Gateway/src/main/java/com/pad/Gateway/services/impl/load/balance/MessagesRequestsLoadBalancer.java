package com.pad.Gateway.services.impl.load.balance;

import com.pad.Gateway.entity.AvailableChatService;
import com.pad.Gateway.services.AvailableServicesLookup;
import messages.ChatRequest;
import messages.GenericReply;
import messages.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@Service
public class MessagesRequestsLoadBalancer {

  @Autowired AvailableServicesLookup availableServicesLookup;

  public GenericReply distributeMessageRequest(messages.SendMessageRequest messageRequest) {
    // TODO implement Round Robin
    List<AvailableChatService> availableChatServices = availableServicesLookup.getAvailableChatServices();

    AvailableChatService availableChatService =
        availableChatServices.get(new Random().nextInt(availableChatServices.size()));

    return availableChatService.sendMessageRequest(messageRequest);
  }

  public LinkedList<Message> distributeChatRequest(ChatRequest chatRequest) {
    List<AvailableChatService> availableChatServices = availableServicesLookup.getAvailableChatServices();

    AvailableChatService availableChatService =
        availableChatServices.get(new Random().nextInt(availableChatServices.size()));

    return availableChatService.sendChatRequest(chatRequest);
  }
}
