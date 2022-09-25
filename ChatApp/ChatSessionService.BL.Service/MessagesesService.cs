using ChatSessionService.BL.Interface;
using ChatSessionService.DAL.Interface;
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
          public async Task InsertMessage(Message message)
          {
               await _messagesRepository.InsertOneAsync(message);
          }

          public async Task<IEnumerable<Message>> GetChatMessages(int requestUserId, int chatUserId)
          {
               var messages = await _messagesRepository.GetChatMessages(requestUserId, chatUserId);
               return messages.OrderBy(x => x.CreatedAt);
          }

          public async Task ChangeMessagesForChatToSeen(int requestUserId, int chatUserId)
          {
               await _messagesRepository.UpdateUserChatMessagesToSeen(requestUserId, chatUserId);
          }
     }
}
