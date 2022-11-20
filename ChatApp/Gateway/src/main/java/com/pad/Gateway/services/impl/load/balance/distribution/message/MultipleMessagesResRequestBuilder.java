package com.pad.Gateway.services.impl.load.balance.distribution.message;

import com.pad.Gateway.dto.message.MessageDto;
import com.pad.Gateway.services.AvailableServicesLookup;
import com.pad.Gateway.services.impl.load.balance.distribution.FinalEntityRequestBuilderAndExecutor;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import messages.Message;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Supplier;

import static com.pad.Gateway.services.impl.load.balance.UsersRequestsLoadBalancer.MAX_REDISTRIBUTION_TRIES;
import static com.pad.Gateway.services.impl.load.balance.distribution.ExceptionHandlerUtil.handleException;

@Slf4j
public class MultipleMessagesResRequestBuilder implements FinalEntityRequestBuilderAndExecutor {
  @Override
  @SuppressWarnings("unchecked")
  public Object createAndExecuteRequest(
      Supplier<Object> messageSupplier, AvailableServicesLookup availableServicesLookup) {
    int servicesTriedCount = 0;
    int servicesToTryCount = availableServicesLookup.getAvailableChatServices().size();

    while (servicesTriedCount < servicesToTryCount
        && servicesTriedCount <= MAX_REDISTRIBUTION_TRIES) {
      try {
        Iterator<Message> messageIterator = (Iterator<Message>) messageSupplier.get();
        LinkedList<MessageDto> messageDtos = new LinkedList<>();

        messageIterator.forEachRemaining(
            chat ->
                messageDtos.add(
                    new MessageDto(
                        chat.getMessageStatus(),
                        chat.getFromUserId(),
                        chat.getToUserId(),
                        chat.getDate(),
                        chat.getMessageContent())));

        return messageDtos;
      } catch (StatusRuntimeException exception) {
        servicesTriedCount++;
        servicesToTryCount = availableServicesLookup.getAvailableChatServices().size();
        handleException(exception);
      }
    }

    log.warn("No available instances found!");

    throw new StatusRuntimeException(Status.UNAVAILABLE.withDescription("No instances available!"));
  }
}
