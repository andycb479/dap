package com.pad.Gateway.services;

import com.pad.Gateway.dto.message.ChatDto;
import com.pad.Gateway.dto.message.MessageDto;
import com.pad.Gateway.dto.message.SendMessageDto;
import com.pad.Gateway.services.impl.MessagesServicesImpl;
import com.pad.Gateway.services.impl.load.balance.MessagesRequestsLoadBalancer;
import messages.GenericReply;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MessageServicesTest {

  @Mock MessagesRequestsLoadBalancer loadBalancer;
  MessagesService messagesService = new MessagesServicesImpl();

  @Mock GenericReply reply;

  @Before
  public void setUp() throws Exception {
    loadBalancer = mock(MessagesRequestsLoadBalancer.class);
    Whitebox.setInternalState(messagesService, "loadBalancer", loadBalancer);
  }

  @Test
  public void testSendMessage() {
    SendMessageDto messageDto = new SendMessageDto("Test message", 1, 2);

    reply = GenericReply.newBuilder().setResponse("Message sent.").build();
    when(loadBalancer.distributeMessageRequest(any(messages.SendMessageRequest.class)))
        .thenReturn(reply);

    GenericReply genericReply = messagesService.sendMessage(messageDto);

    assertEquals("Message sent.", genericReply.getResponse());
  }

  @Test
  public void testSendChatRequest() throws Exception {
    ChatDto chatDto = new ChatDto(1, 2);

    messages.ChatRequest request =
        messages.ChatRequest.newBuilder()
            .setRequestUserId(chatDto.getRequestUserId())
            .setChatUserId(chatDto.getChatUserId())
            .build();

    LinkedList<MessageDto> receivedMessages = new LinkedList<>();
    receivedMessages.add(new MessageDto(1, 1, 2, null, "Message Content 1"));
    receivedMessages.add(new MessageDto(1, 1, 2, null, "Message Content 2"));

    when(loadBalancer.distributeChatRequest(any(messages.ChatRequest.class))).thenReturn(receivedMessages);
    LinkedList<MessageDto> messageDtos = messagesService.getChat(chatDto);

    assertEquals(messageDtos.get(0).getMessageContent(), "Message Content 1");
    assertEquals(messageDtos.get(1).getMessageContent(), "Message Content 2");
  }
}
