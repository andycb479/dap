package com.pad.Gateway.services;

import com.pad.Gateway.dto.message.ChatDto;
import com.pad.Gateway.dto.message.MessageDto;
import com.pad.Gateway.dto.message.SendMessageDto;
import messages.GenericReply;

import java.util.LinkedList;

public interface MessagesService {
  GenericReply sendMessage(SendMessageDto messageDto);

  LinkedList<MessageDto> getChat(ChatDto chatDto);

  void changeStatusToSeen(ChatDto chatDto);
}
