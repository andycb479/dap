package com.pad.Gateway.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChatDto {
  private int requestUserId;
  private int chatUserId;
}
