package com.pad.Gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class MessageDto {
    private int messageStatus;
    private int fromUserId;
    private int toUserId;
    private String date;
    private String messageContent;
}

/**
 * int32 messageStatus = 1; int32 fromUserId = 2; int32 toUserId = 3; string date = 4; string messageContent = 5;
 */
