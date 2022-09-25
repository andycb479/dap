using ChatSessionService.DAL.Interface;
using MongoDB.Driver;
using Services.Infrastructure.Configurations;
using Services.Infrastructure.Entity;
using Services.Infrastructure.Enums;

namespace ChatSessionService.DAL.Service
{
     public class MessagesRepository : MongoRepository<MessageEntity>, IMessagesRepository
     {
          public MessagesRepository(IMongoDbSettings settings) : base(settings)
          {
          }

          public async Task<IEnumerable<MessageEntity>> GetChatMessages(int requestUserId, int chatUserId)
          {
               var messages = await FilterByAsync(x =>
                    x.FromUserId == requestUserId && x.ToUserId == chatUserId ||
                    x.FromUserId == chatUserId && x.ToUserId == requestUserId);
               return messages;
          }

          public async Task UpdateUserChatMessagesToSeen(int requestUserId, int chatUserId)
          {
               var builder = Builders<MessageEntity>.Filter;
               var filter = builder.Eq(_ => _.FromUserId, chatUserId);
               filter &= builder.Eq(_ => _.ToUserId, requestUserId);

               var update = Builders<MessageEntity>.Update.Set(x => x.MessageStatus, MessageStatus.Seen);

               await Collection.UpdateManyAsync(filter, update);
          }
     }
}
