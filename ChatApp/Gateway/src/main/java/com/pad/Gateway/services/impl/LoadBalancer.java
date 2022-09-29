package com.pad.Gateway.services.impl;

import com.pad.Gateway.entity.AvailableService;
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
public class LoadBalancer {

  @Autowired AvailableServicesLookup availableServicesLookup;

  public GenericReply distributeMessageRequest(messages.SendMessageRequest messageRequest) {
    // TODO implement Round Robin
    List<AvailableService> availableServices = availableServicesLookup.getAvailableServices();

    AvailableService availableService =
        availableServices.get(new Random().nextInt(availableServices.size()));

    return availableService.sendMessageRequest(messageRequest);
  }

  public LinkedList<Message> distributeChatRequest(ChatRequest chatRequest) {
    List<AvailableService> availableServices = availableServicesLookup.getAvailableServices();

    AvailableService availableService =
        availableServices.get(new Random().nextInt(availableServices.size()));

    return availableService.sendChatRequest(chatRequest);
  }
}
