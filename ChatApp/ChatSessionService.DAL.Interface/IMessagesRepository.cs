using Services.Infrastructure.Entity;

namespace ChatSessionService.DAL.Interface
{
     public interface IMessagesRepository : IMongoRepository<Message>
     {
          Task<IEnumerable<Message>> GetChatMessages(int requestUserId, int chatUserId);
          Task UpdateUserChatMessagesToSeen(int requestUserId, int chatUserId);
     }
}
