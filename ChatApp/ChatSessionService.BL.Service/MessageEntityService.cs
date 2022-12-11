using ChatSessionService.BL.Interface;
using ChatSessionService.DAL.Interface;
using ExternalServices.Services;
using Microsoft.Extensions.Configuration;
using Services.Core.Caching.Interface;
using Services.Infrastructure.Entity;
using Services.Infrastructure.Enums;
using Services.Infrastructure.Exceptions;

namespace ChatSessionService.BL.Service
{
     public class MessageEntityService : IMessageEntityService
     {
          private readonly IMessagesRepository _messagesRepository;
          private readonly IUsersService _usersService;
          private readonly ICacheService _cacheService;

          private readonly string _serviceName;

          public MessageEntityService(IMessagesRepository messagesRepository, IUsersService usersService, ICacheService cacheService, IConfiguration configuration)
          {
               _messagesRepository = messagesRepository;
               _usersService = usersService;
               _cacheService = cacheService;

               _serviceName = configuration.GetValue<string>("ServiceConfig:Name") ?? "ChatSessionService";
          }

          public async Task Insert(MessageEntity message)
          {
               await ValidateMessage(message);

               await RemoveChatFromCacheIfPresent(message.FromUserId, message.ToUserId);

               await _messagesRepository.InsertOneAsync(message);
          }

          public async Task<IEnumerable<MessageEntity>> GetChatMessages(int requestUserId, int chatUserId)
          {
               await ValidateUsers(requestUserId, chatUserId);

               var chatCacheKey = CreateChatCacheKey(requestUserId, chatUserId);
               var messages = await _cacheService.GetFromCacheAsync<IEnumerable<MessageEntity>>(chatCacheKey);
               if (messages is not null)
               {
                    if (HasUnseenMessagesFromRequestingUser(messages, requestUserId, chatUserId))
                    {
                         await RemoveChatFromCacheIfPresent(requestUserId, chatUserId);
                    }
                    else
                    { 
                         return messages;
                    }
               }

               await ChangeMessagesForChatToSeen(requestUserId, chatUserId);

               messages = await _messagesRepository.GetChatMessages(requestUserId, chatUserId);
               messages = messages.OrderBy(x => x.CreatedAt).ToList();

               await _cacheService.SetInCacheAsync(messages, chatCacheKey, CacheExpiryType.TwoMinutes);

               return messages;
          }

          public async Task ChangeMessagesForChatToSeen(int requestUserId, int chatUserId)
          {
               await _messagesRepository.UpdateUserChatMessagesToSeen(requestUserId, chatUserId);
          }

          public async Task DeleteUserChats(int userId)
          {
               await _messagesRepository.DeleteUserChats(userId);
          }

          private string CreateChatCacheKey(int requestUserId, int chatUserId)
          {
               var chatId = GenerateChatId(requestUserId, chatUserId);
               return _cacheService.CreateCacheKey(_serviceName, typeof(IEnumerable<MessageEntity>), chatId, String.Empty);
          }

          private bool HasUnseenMessagesFromRequestingUser(IEnumerable<MessageEntity> messages, int requestUserId, int chatUserId)
          {
               return messages.Any(x => x.ToUserId == requestUserId && x.FromUserId == chatUserId && x.MessageStatus == MessageStatus.Sent);
          }

          private async Task RemoveChatFromCacheIfPresent(int requestUserId, int chatUserId)
          {
               var chatCacheKey = CreateChatCacheKey(requestUserId, chatUserId);
               var messages = await _cacheService.GetFromCacheAsync<IEnumerable<MessageEntity>>(chatCacheKey);

               if (messages is not null) await _cacheService.RemoveAsync(chatCacheKey);
          }

          private async Task ValidateMessage(MessageEntity message)
          {
               if (string.IsNullOrWhiteSpace(message.MessageContent))
               {
                    throw new ValidationException("Message content cannot be empty.");
               }

               await ValidateUsers(message.FromUserId, message.ToUserId);
          }

          private async Task ValidateUsers(int requestUserId, int chatUserId)
          {
               if (requestUserId <= 0)
               {
                    throw new ValidationException("Invalid sender.");
               }

               if (chatUserId <= 0)
               {
                    throw new ValidationException("Invalid receiver.");
               }

               var fromUser = await _usersService.GetUserAsync(_serviceName, requestUserId);
               if (fromUser is null)
               {
                    throw new ValidationException("Sender user cannot be found!");
               }

               var toUser = await _usersService.GetUserAsync(_serviceName, chatUserId);
               if (toUser is null)
               {
                    throw new ValidationException("Receiver user cannot be found!");
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
