﻿using Services.Infrastructure.Entity;

namespace ChatSessionService.BL.Interface
{
     public interface IMessagesService
     {
          Task InsertMessage(Message message);
          Task<IEnumerable<Message>> GetChatMessages(int requestUserId, int chatUserId);
          Task ChangeMessagesForChatToSeen(int requestUserId, int chatUserId);
     }
}
