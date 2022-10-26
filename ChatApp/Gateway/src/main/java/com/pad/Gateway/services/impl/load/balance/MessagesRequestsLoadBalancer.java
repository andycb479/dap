package com.pad.Gateway.services.impl.load.balance;

import com.pad.Gateway.entity.AvailableChatService;
import com.pad.Gateway.services.AvailableServicesLookup;
import lombok.extern.slf4j.Slf4j;
import messages.ChatRequest;
import messages.GenericReply;
import messages.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class MessagesRequestsLoadBalancer {

  @Autowired AvailableServicesLookup availableServicesLookup;

  private static final AtomicInteger ind = new AtomicInteger(0);

  public GenericReply distributeMessageRequest(messages.SendMessageRequest messageRequest) {
    List<AvailableChatService> availableChatServices =
        availableServicesLookup.getAvailableChatServices();

    int serviceIndex = ind.getAndAccumulate(availableChatServices.size(), (cur, n)->cur >= n-1 ? 0 : cur+1);
    AvailableChatService availableChatService = availableChatServices.get(serviceIndex);

    log.info(
        "Sending request to users service with address: "
            + availableChatService.getAddress()
            + ":"
            + availableChatService.getPort());

    return availableChatService.sendMessageRequest(messageRequest);
  }

  public LinkedList<Message> distributeChatRequest(ChatRequest chatRequest) {
    List<AvailableChatService> availableChatServices =
        availableServicesLookup.getAvailableChatServices();

    int serviceIndex = ind.getAndAccumulate(availableChatServices.size(), (cur, n)->cur >= n-1 ? 0 : cur+1);
    AvailableChatService availableChatService = availableChatServices.get(serviceIndex);

    log.info(
        "Sending request to users service with address: "
            + availableChatService.getAddress()
            + ":"
            + availableChatService.getPort());

    return availableChatService.sendChatRequest(chatRequest);
  }
}
