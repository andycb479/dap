using Services.Infrastructure.Entity;

namespace ChatSessionService.BL.Interface
{
     public interface IMessageEntityService
     {
          Task Insert(MessageEntity message);
          Task<IEnumerable<MessageEntity>> GetChatMessages(int requestUserId, int chatUserId);
          Task ChangeMessagesForChatToSeen(int requestUserId, int chatUserId);
          Task DeleteUserChats(int userId);
     }
}
