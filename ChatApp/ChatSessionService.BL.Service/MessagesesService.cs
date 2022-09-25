using ChatSessionService.BL.Interface;
using ChatSessionService.DAL.Interface;
using Services.Infrastructure;
using Services.Infrastructure.Entity;

namespace ChatSessionService.BL.Service
{
     public class MessagesService : IMessagesService
     {
          private readonly IMessagesRepository _messagesRepository;

          public MessagesService(IMessagesRepository messagesRepository)
          {
               _messagesRepository = messagesRepository;
          }
          public async Task InsertMessage(MessageEntity message)
          {
               ValidateMessage(message);
               await _messagesRepository.InsertOneAsync(message);
          }

          public async Task<IEnumerable<MessageEntity>> GetChatMessages(int requestUserId, int chatUserId)
          {
               var messages = await _messagesRepository.GetChatMessages(requestUserId, chatUserId);
               return messages.OrderBy(x => x.CreatedAt);
          }

          public async Task ChangeMessagesForChatToSeen(int requestUserId, int chatUserId)
          {
               await _messagesRepository.UpdateUserChatMessagesToSeen(requestUserId, chatUserId);
          }

          private void ValidateMessage(MessageEntity message)
          {
               if (message.FromUserId <= 0)
               {
                    throw new ValidationException("Invalid sender.");
               }

               if (message.ToUserId <= 0)
               {
                    throw new ValidationException("Invalid receiver.");
               }

               if (string.IsNullOrWhiteSpace(message.MessageContent))
               {
                    throw new ValidationException("Message content cannot be empty.");
               }
          }
     }
}
