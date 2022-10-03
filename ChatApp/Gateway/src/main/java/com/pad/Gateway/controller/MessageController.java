package com.pad.Gateway.controller;

import com.pad.Gateway.dto.message.ChatDto;
import com.pad.Gateway.dto.message.MessageDto;
import com.pad.Gateway.dto.message.SendMessageDto;
import com.pad.Gateway.services.MessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

  @Autowired MessagesService messagesService;

  @PostMapping
  public String sendMessage(@RequestBody SendMessageDto sendMessageDto) {
    return messagesService.sendMessage(sendMessageDto).getResponse();
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public List<MessageDto> getChat(@RequestBody ChatDto chatDto) {
    return messagesService.getChat(chatDto);
  }

  @PutMapping
  public void changeMessagesForChatToSeen(@RequestBody ChatDto chatDto) {
    messagesService.changeStatusToSeen(chatDto);
  }
}
