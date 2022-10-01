package com.pad.Gateway.services;

import com.pad.Gateway.dto.ChatDto;
import com.pad.Gateway.dto.MessageDto;
import com.pad.Gateway.dto.SendMessageDto;
import messages.GenericReply;

import java.util.LinkedList;

public interface MessagesService {
  GenericReply sendMessage(SendMessageDto messageDto);

  LinkedList<MessageDto> getChat(ChatDto chatDto);

  void changeStatusToSeen(ChatDto chatDto);
}
