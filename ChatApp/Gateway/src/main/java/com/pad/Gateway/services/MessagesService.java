package com.pad.Gateway.services;

import com.pad.Gateway.dto.ChatDto;
import com.pad.Gateway.dto.SendMessageDto;
import messages.GenericReply;
import messages.Message;

import java.util.LinkedList;

public interface MessagesService {
  GenericReply sendMessage(SendMessageDto messageDto);

  LinkedList<Message> getChat(ChatDto chatDto);

  void changeStatusToSeen(ChatDto chatDto);
}
