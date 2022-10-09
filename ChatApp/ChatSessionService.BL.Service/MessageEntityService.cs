using ChatSessionService.BL.Interface;
using ChatSessionService.DAL.Interface;
using Microsoft.Extensions.Configuration;
using Services.Core.Caching.Interface;
using Services.Infrastructure;
using Services.Infrastructure.Entity;
using Services.Infrastructure.Enums;

namespace ChatSessionService.BL.Service
{
     public class MessageEntityService : IMessageEntityService
     {
          private readonly IMessagesRepository _messagesRepository;
          private readonly ICacheService _cacheService;

          private readonly string _serviceName;

          public MessageEntityService(IMessagesRepository messagesRepository, ICacheService cacheService, IConfiguration configuration)
          {
               _messagesRepository = messagesRepository;
               _cacheService = cacheService;

               _serviceName = configuration.GetValue<string>("ServiceConfig:Name") ?? "ChatSessionService";
          }
          public async Task Insert(MessageEntity message)
          {
               ValidateMessage(message);

               await RemoveChatFromCacheIfPresent(message.FromUserId, message.ToUserId);

               await _messagesRepository.InsertOneAsync(message);
          }

          public async Task<IEnumerable<MessageEntity>> GetChatMessages(int requestUserId, int chatUserId)
          {
               var chatCacheKey = CreateChatCacheKey(requestUserId, chatUserId);
               var messages = await _cacheService.GetFromCacheAsync<IEnumerable<MessageEntity>>(chatCacheKey);

               if (messages is not null) return messages;

               await ChangeMessagesForChatToSeen(requestUserId, chatUserId);

               messages = await _messagesRepository.GetChatMessages(requestUserId, chatUserId);
               messages = messages.OrderBy(x => x.CreatedAt).ToList();

               await _cacheService.SetInCacheAsync(messages, chatCacheKey, CacheExpiryType.TenMinutes);

               return messages;
          }

          public async Task ChangeMessagesForChatToSeen(int requestUserId, int chatUserId)
          {
               await _messagesRepository.UpdateUserChatMessagesToSeen(requestUserId, chatUserId);
          }

          private string CreateChatCacheKey(int requestUserId, int chatUserId)
          {
               var chatId = GenerateChatId(requestUserId, chatUserId);
               return _cacheService.CreateCacheKey(_serviceName, typeof(IEnumerable<MessageEntity>), chatId, String.Empty);
          }

          private async Task RemoveChatFromCacheIfPresent(int requestUserId, int chatUserId)
          {
               var chatCacheKey = CreateChatCacheKey(requestUserId, chatUserId);
               var messages = await _cacheService.GetFromCacheAsync<IEnumerable<MessageEntity>>(chatCacheKey);

               if (messages is not null) await _cacheService.RemoveAsync(chatCacheKey);
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

          private string GenerateChatId(int requestUserId, int chatUserId)
          {
               var users = new[] { requestUserId, chatUserId };
               Array.Sort(users);
               return $"{users[0]}:{users[1]}";
          }
     }
}
