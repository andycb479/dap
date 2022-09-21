using ChatSessionService.DAL.Interface;
using ChatSessionService.Infrastructure.Configurations;
using ChatSessionService.Infrastructure.Entity;
using ChatSesssionService.DAL.Service;

namespace ChatSessionService.DAL.Service
{
     public class MessagesRepository : MongoRepository<Message>, IMessagesRepository
     {
          public MessagesRepository(IMongoDbSettings settings) : base(settings)
          {
          }

          public async Task<IEnumerable<Message>> GetChatMessages(int requestUserId, int chatUserId)
          {
               var messages = await FilterByAsync(x =>
                    x.FromUserId == requestUserId && x.ToUserId == chatUserId ||
                    x.FromUserId == chatUserId && x.ToUserId == requestUserId);
               return messages;
          }
     }
}
