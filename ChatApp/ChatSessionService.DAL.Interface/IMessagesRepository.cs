using Services.Infrastructure.Entity;

namespace ChatSessionService.DAL.Interface
{
     public interface IMessagesRepository : IMongoRepository<MessageEntity>
     {
          Task<IEnumerable<MessageEntity>> GetChatMessages(int requestUserId, int chatUserId);
          Task UpdateUserChatMessagesToSeen(int requestUserId, int chatUserId);
          Task DeleteUserChats(int userId);
     }
}
