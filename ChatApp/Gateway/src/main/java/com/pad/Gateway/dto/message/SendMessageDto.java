package com.pad.Gateway.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SendMessageDto {
  private String messageContent;
  private int fromUserId;
  private int toUserId;
}
