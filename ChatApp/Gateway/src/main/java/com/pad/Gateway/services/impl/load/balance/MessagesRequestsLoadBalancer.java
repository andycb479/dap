package com.pad.Gateway.services.impl.load.balance;

import com.pad.Gateway.dto.message.MessageDto;
import com.pad.Gateway.entity.AvailableChatService;
import com.pad.Gateway.services.AvailableServicesLookup;
import com.pad.Gateway.services.impl.load.balance.distribution.FinalEntityRequestBuilderAndExecutor;
import com.pad.Gateway.services.impl.load.balance.distribution.message.MultipleMessagesResRequestBuilder;
import com.pad.Gateway.services.impl.load.balance.distribution.message.SingleMessageResRequestBuilder;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import messages.ChatRequest;
import messages.GenericReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

@Service
@Slf4j
public class MessagesRequestsLoadBalancer {

  @Autowired AvailableServicesLookup availableServicesLookup;

  private static final AtomicInteger ind = new AtomicInteger(0);

  public static final int MAX_REDISTRIBUTION_TRIES = 10;

  private final FinalEntityRequestBuilderAndExecutor singleMessageResRequestBuilder =
      new SingleMessageResRequestBuilder();
  private final FinalEntityRequestBuilderAndExecutor multipleMessagesResRequestBuilder =
      new MultipleMessagesResRequestBuilder();

  private AvailableChatService getNextAvailableService() {
    List<AvailableChatService> availableChatServices =
        availableServicesLookup.getAvailableChatServices();

    int serviceIndex =
        ind.getAndAccumulate(availableChatServices.size(), (cur, n) -> cur >= n - 1 ? 0 : cur + 1);
    AvailableChatService availableChatService = availableChatServices.get(serviceIndex);

    log.info(
        "Sending request to chat service with address: "
            + availableChatService.getAddress()
            + ":"
            + availableChatService.getPort());

    return availableChatService;
  }

  public GenericReply distributeMessageRequest(messages.SendMessageRequest messageRequest) {
    Supplier<Object> messageSupplier =
        () -> getNextAvailableService().sendMessageRequest(messageRequest);
    return (GenericReply)
        singleMessageResRequestBuilder.createAndExecuteRequest(
            messageSupplier, availableServicesLookup);
  }

  @SuppressWarnings("unchecked")
  public LinkedList<MessageDto> distributeChatRequest(ChatRequest chatRequest)
      throws StatusRuntimeException {
    Supplier<Object> messageSupplier = () -> getNextAvailableService().sendChatRequest(chatRequest);
    return (LinkedList<MessageDto>)
        multipleMessagesResRequestBuilder.createAndExecuteRequest(
            messageSupplier, availableServicesLookup);
  }
}
