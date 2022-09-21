using ChatSessionService.Infrastructure.Entity;

namespace ChatSessionService.DAL.Interface
{
     public interface IMessagesRepository : IMongoRepository<Message>
     {
          Task<IEnumerable<Message>> GetChatMessages(int requestUserId, int chatUserId);
     }
}
