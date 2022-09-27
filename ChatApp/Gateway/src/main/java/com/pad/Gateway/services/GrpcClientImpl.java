package com.pad.Gateway.services;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import messages.GenericReply;
import messages.MessagesGrpc;
import messages.SendMessageRequest;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GrpcClientImpl {
  @EventListener(ApplicationReadyEvent.class)
  public void initGrpcClient() throws InterruptedException {
    ManagedChannel channel =
        ManagedChannelBuilder.forTarget("localhost:9100").usePlaintext().build();
    MessagesGrpc.MessagesBlockingStub stub = MessagesGrpc.newBlockingStub(channel);

    SendMessageRequest request =
        SendMessageRequest.newBuilder()
            .setMessageContent("WAAAAII")
            .setFromUserId(1)
            .setToUserId(2)
            .build();

    GenericReply reply = stub.sendMessage(request);
    log.info(reply.getResponse());
  }
}
