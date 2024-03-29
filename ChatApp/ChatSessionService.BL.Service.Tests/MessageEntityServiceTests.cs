﻿using ChatSessionService.BL.Interface;
using ChatSessionService.DAL.Interface;
using ExternalServices;
using ExternalServices.Services;
using Microsoft.Extensions.Configuration;
using MongoDB.Bson;
using Moq;
using Services.Core.Caching.Interface;
using Services.Infrastructure.Entity;
using Services.Infrastructure.Enums;
using Services.Infrastructure.Exceptions;
using Xunit;

namespace ChatSessionService.BL.Service.Tests
{
    public class MessageEntityServiceTests
     {
          private readonly Mock<IMessagesRepository> _messagesRepository;
          private readonly Mock<IUsersService> _usersService;
          private readonly Mock<ICacheService> _cacheService;
          private readonly IMessageEntityService _messageEntityService;

          public MessageEntityServiceTests()
          {
               _cacheService = new Mock<ICacheService>();
               _messagesRepository = new Mock<IMessagesRepository>();
               _usersService = new Mock<IUsersService>();

               Mock<IConfiguration> configuration = new Mock<IConfiguration>();
               configuration.Setup(c => c.GetSection(It.IsAny<String>())).Returns(new Mock<IConfigurationSection>().Object);

               TestClassSetup();

               _messageEntityService = new MessageEntityService(_messagesRepository.Object, _usersService.Object, _cacheService.Object, configuration.Object);
          }

          private void TestClassSetup()
          {

          }

          [Fact]
          public async Task Insert_FromUserIdIsNegative_ThrowsValidationException()
          {
               var message = new MessageEntity() { FromUserId = -1, MessageContent = "Test"};

               var result = await Assert.ThrowsAsync<ValidationException>(async () => await _messageEntityService.Insert(message));

               Assert.Equal("Invalid sender.", result.Message);
          }

          [Fact]
          public async Task Insert_ToUserIdIsNegative_ThrowsValidationException()
          {
               var message = new MessageEntity() { FromUserId = 1, ToUserId = -1, MessageContent = "Test" };

               var result = await Assert.ThrowsAsync<ValidationException>(async () => await _messageEntityService.Insert(message));

               Assert.Equal("Invalid receiver.", result.Message);
          }

          [Fact]
          public async Task Insert_MessageContentIsNull_ThrowsValidationException()
          {
               var message = new MessageEntity() { ToUserId = 1, FromUserId = 2, MessageContent = null };

               var result = await Assert.ThrowsAsync<ValidationException>(async () => await _messageEntityService.Insert(message));

               Assert.Equal("Message content cannot be empty.", result.Message);
          }

          [Fact]
          public async Task Insert_MessageContentIsEmpty_ThrowsValidationException()
          {
               var message = new MessageEntity() { ToUserId = 1, FromUserId = 2, MessageContent = "" };

               var result = await Assert.ThrowsAsync<ValidationException>(async () => await _messageEntityService.Insert(message));

               Assert.Equal("Message content cannot be empty.", result.Message);
          }

          [Fact]
          public async Task Insert_ReceiverUserIsNotFound_ThrowsValidationException()
          {
               var toUserId = 3;
               var fromUserId = 1;
               var message = new MessageEntity() { ToUserId = toUserId, FromUserId = fromUserId, MessageContent = "Test" };
               _usersService.Setup(x => x.GetUserAsync(toUserId)).ReturnsAsync(value: null);
               _usersService.Setup(x => x.GetUserAsync(fromUserId)).ReturnsAsync(new User());

               var result = await Assert.ThrowsAsync<ValidationException>(async () => await _messageEntityService.Insert(message));

               Assert.Equal("Receiver user cannot be found!", result.Message);
          }

          [Fact]
          public async Task Insert_SenderUserIsNotFound_ThrowsValidationException()
          {
               var toUserId = 3;
               var fromUserId = 1;
               var message = new MessageEntity() { ToUserId = toUserId, FromUserId = fromUserId, MessageContent = "Test" };
               _usersService.Setup(x => x.GetUserAsync(fromUserId)).ReturnsAsync(value: null);
               _usersService.Setup(x => x.GetUserAsync(toUserId)).ReturnsAsync(new User());

               var result = await Assert.ThrowsAsync<ValidationException>(async () => await _messageEntityService.Insert(message));

               Assert.Equal("Sender user cannot be found!", result.Message);
          }

          [Fact]
          public async Task Insert_MessageContentPresent_CallsCreateCacheKeyWithCorrectChatId()
          {
               var message = new MessageEntity() { ToUserId = 10, FromUserId = 20, MessageContent = "Message" };
               var expectedChatId = "10:20";
               _usersService.Setup(x => x.GetUserAsync(It.IsAny<int>())).ReturnsAsync(new User());

               await _messageEntityService.Insert(message);

               _cacheService.Verify(x => x.CreateCacheKey(It.IsAny<string>(), typeof(IEnumerable<MessageEntity>), expectedChatId, It.IsAny<string>()));
          }

          [Fact]
          public async Task Insert_MessageContentPresent_CallsGetFromCacheAsyncWithCorrectCacheKey()
          {
               var message = new MessageEntity() { ToUserId = 10, FromUserId = 20, MessageContent = "Message" };
               var expectedCacheKey = "cacheKey";
               _cacheService.Setup(x => x.CreateCacheKey(It.IsAny<string>(), typeof(IEnumerable<MessageEntity>),
                    It.IsAny<string>(), It.IsAny<string>())).Returns(expectedCacheKey);
               _usersService.Setup(x => x.GetUserAsync(It.IsAny<int>())).ReturnsAsync(new User());

               await _messageEntityService.Insert(message);

               _cacheService.Verify(x => x.GetFromCacheAsync<IEnumerable<MessageEntity>>(expectedCacheKey));
          }

          [Fact]
          public async Task Insert_MessageNotPresentInCache_DoesNotCallRemoveAsync()
          {
               var message = new MessageEntity() { ToUserId = 10, FromUserId = 20, MessageContent = "Message" };
               _cacheService.Setup(x => x.GetFromCacheAsync<IEnumerable<MessageEntity>>(It.IsAny<string>()))
                    .ReturnsAsync(value: null);
               _usersService.Setup(x => x.GetUserAsync(It.IsAny<int>())).ReturnsAsync(new User());

               await _messageEntityService.Insert(message);

               _cacheService.Verify(x => x.RemoveAsync(It.IsAny<string>()), Times.Never);
          }

          [Fact]
          public async Task Insert_MessagePresentInCache_CallsRemoveAsync()
          {
               var message = new MessageEntity() { ToUserId = 10, FromUserId = 20, MessageContent = "Message" };
               _cacheService.Setup(x => x.GetFromCacheAsync<IEnumerable<MessageEntity>>(It.IsAny<string>()))
                    .ReturnsAsync(new List<MessageEntity>());
               _usersService.Setup(x => x.GetUserAsync(It.IsAny<int>())).ReturnsAsync(new User());

               await _messageEntityService.Insert(message);

               _cacheService.Verify(x => x.RemoveAsync(It.IsAny<string>()), Times.Once);
          }

          [Fact]
          public async Task Insert_MessageContentPresent_CallsInsertOneAsync()
          {
               var message = new MessageEntity() { ToUserId = 10, FromUserId = 20, MessageContent = "Message" };
               _usersService.Setup(x => x.GetUserAsync(It.IsAny<int>())).ReturnsAsync(new User());

               await _messageEntityService.Insert(message);

               _messagesRepository.Verify(x => x.InsertOneAsync(It.IsAny<MessageEntity>()), Times.Once);
          }

          [Fact]
          public async Task ChangeMessagesForChatToSeen_CallsUpdateUserChatMessagesToSeenWithCorrectArguments()
          {
               var requestUserId = 1;
               var chatUserId = 2;

               await _messageEntityService.ChangeMessagesForChatToSeen(requestUserId, chatUserId);

               _messagesRepository.Verify(x => x.UpdateUserChatMessagesToSeen(requestUserId, chatUserId), Times.Once);
          }

          [Fact]
          public async Task GetChatMessages_ReceiverUserIsNotFound_ThrowsValidationException()
          {
               var toUserId = 3;
               var fromUserId = 1;
               var message = new MessageEntity() { ToUserId = toUserId, FromUserId = fromUserId, MessageContent = "Test" };
               _usersService.Setup(x => x.GetUserAsync(toUserId)).ReturnsAsync(value: null);
               _usersService.Setup(x => x.GetUserAsync(fromUserId)).ReturnsAsync(new User());

               var result = await Assert.ThrowsAsync<ValidationException>(async () => await _messageEntityService.GetChatMessages(fromUserId, toUserId));

               Assert.Equal("Receiver user cannot be found!", result.Message);
          }

          [Fact]
          public async Task GetChatMessages_SenderUserIsNotFound_ThrowsValidationException()
          {
               var toUserId = 3;
               var fromUserId = 1;
               _usersService.Setup(x => x.GetUserAsync(fromUserId)).ReturnsAsync(value: null);
               _usersService.Setup(x => x.GetUserAsync(toUserId)).ReturnsAsync(new User());

               var result = await Assert.ThrowsAsync<ValidationException>(async () => await _messageEntityService.GetChatMessages(fromUserId, toUserId));

               Assert.Equal("Sender user cannot be found!", result.Message);
          }

          [Fact]
          public async Task GetChatMessages_CallsCreateCacheKeyWithExpectedChatId()
          {
               var requestUserId = 1;
               var chatUserId = 2;
               var expectedChatId = "1:2";
               _usersService.Setup(x => x.GetUserAsync(It.IsAny<int>())).ReturnsAsync(new User());

               await _messageEntityService.GetChatMessages(requestUserId, chatUserId);

               _cacheService.Verify(x => x.CreateCacheKey(It.IsAny<string>(), typeof(IEnumerable<MessageEntity>), expectedChatId, It.IsAny<string>()));
          }

          [Fact]
          public async Task GetChatMessages_CallsGetFromCacheAsyncWithCorrectCacheKey()
          {
               var requestUserId = 1;
               var chatUserId = 2;
               var expectedCacheKey = "cacheKey";
               _cacheService.Setup(x => x.CreateCacheKey(It.IsAny<string>(), typeof(IEnumerable<MessageEntity>),
                    It.IsAny<string>(), It.IsAny<string>())).Returns(expectedCacheKey);
               _usersService.Setup(x => x.GetUserAsync(It.IsAny<int>())).ReturnsAsync(new User());

               await _messageEntityService.GetChatMessages(requestUserId, chatUserId);

               _cacheService.Verify(x => x.GetFromCacheAsync<IEnumerable<MessageEntity>>(expectedCacheKey));
          }

          [Fact]
          public async Task GetChatMessages_MessagesPresentInCache_ReturnCachedMessages()
          {
               var requestUserId = 1;
               var chatUserId = 2;
               var cacheMessages = new List<MessageEntity>();
               _cacheService.Setup(x => x.GetFromCacheAsync<IEnumerable<MessageEntity>>(It.IsAny<string>()))
                    .ReturnsAsync(cacheMessages);
               _usersService.Setup(x => x.GetUserAsync(It.IsAny<int>())).ReturnsAsync(new User());

               var result = await _messageEntityService.GetChatMessages(requestUserId, chatUserId);

               Assert.Equal(cacheMessages, result);
          }

          [Fact]
          public async Task GetChatMessages_MessagesNotPresentInCache_CallsUpdateUserChatMessagesToSeenWithCorrectArguments()
          {
               var requestUserId = 1;
               var chatUserId = 2;
               _cacheService.Setup(x => x.GetFromCacheAsync<IEnumerable<MessageEntity>>(It.IsAny<string>()))
                    .ReturnsAsync(value: null);
               _usersService.Setup(x => x.GetUserAsync(It.IsAny<int>())).ReturnsAsync(new User());

               await _messageEntityService.GetChatMessages(requestUserId, chatUserId);

               _messagesRepository.Verify(x => x.UpdateUserChatMessagesToSeen(requestUserId, chatUserId), Times.Once);
          }

          [Fact]
          public async Task GetChatMessages_MesssagesPresentInCache_UserHasUnseenMessages_MessagesAreRemovedFromCache()
          {
               var requestUserId = 1;
               var chatUserId = 2;
               _cacheService.Setup(x => x.GetFromCacheAsync<IEnumerable<MessageEntity>>(It.IsAny<string>()))
                    .ReturnsAsync(new List<MessageEntity>()
                    {
                         new ()
                         {
                              FromUserId = 2, ToUserId = 1, MessageStatus = MessageStatus.Sent
                         },
                         new ()
                         {
                              FromUserId = 2, ToUserId = 1, MessageStatus = MessageStatus.Sent
                         },
                    });
               _usersService.Setup(x => x.GetUserAsync(It.IsAny<int>())).ReturnsAsync(new User());

               await _messageEntityService.GetChatMessages(requestUserId, chatUserId);

               _cacheService.Verify(x => x.RemoveAsync(It.IsAny<string>()), Times.Once);
          }

          [Fact]
          public async Task GetChatMessages_MesssagesPresentInCache_UserDoesNotHaveUnseenMessages_ExpectedMessagesAreReturned()
          {
               var requestUserId = 1;
               var chatUserId = 2;
               _cacheService.Setup(x => x.GetFromCacheAsync<IEnumerable<MessageEntity>>(It.IsAny<string>()))
                    .ReturnsAsync(new List<MessageEntity>()
                    {
                         new ()
                         {
                              FromUserId = 1, ToUserId = 2, MessageStatus = MessageStatus.Sent
                         },
                         new ()
                         {
                              FromUserId = 1, ToUserId = 2, MessageStatus = MessageStatus.Sent
                         },
                    });
               _usersService.Setup(x => x.GetUserAsync(It.IsAny<int>())).ReturnsAsync(new User());

               var result = await _messageEntityService.GetChatMessages(requestUserId, chatUserId);

               Assert.Equal(MessageStatus.Sent, result.First().MessageStatus);
               Assert.Equal(MessageStatus.Sent, result.ElementAt(1).MessageStatus);
          }

          [Fact]
          public async Task GetChatMessages_MessagesNotPresentInCache_CallsGetChatMessagesWithCorrectArguments()
          {
               var requestUserId = 1;
               var chatUserId = 2;
               _cacheService.Setup(x => x.GetFromCacheAsync<IEnumerable<MessageEntity>>(It.IsAny<string>()))
                    .ReturnsAsync(value: null);
               _usersService.Setup(x => x.GetUserAsync(It.IsAny<int>())).ReturnsAsync(new User());

               await _messageEntityService.GetChatMessages(requestUserId, chatUserId);

               _messagesRepository.Verify(x => x.GetChatMessages(requestUserId, chatUserId), Times.Once);
          }

          [Fact]
          public async Task GetChatMessages_MessagesNotPresentInCache_CallsSetInCacheAsyncWithOrderedMessages()
          {
               var requestUserId = 1;
               var chatUserId = 2;
               var messages = new List<MessageEntity>()
               {
                    new()
                    {
                         Id = ObjectId.GenerateNewId(DateTime.UtcNow), MessageContent = "SecondMessage"
                    },
                    new()
                    {
                         Id = ObjectId.GenerateNewId(DateTime.UtcNow.AddDays(-1)),
                         MessageContent = "FirstMessage"
                    },
               };
               _cacheService.Setup(x => x.GetFromCacheAsync<IEnumerable<MessageEntity>>(It.IsAny<string>()))
                    .ReturnsAsync(value: null);
               _messagesRepository.Setup(x => x.GetChatMessages(requestUserId, chatUserId)).ReturnsAsync(messages);
               _usersService.Setup(x => x.GetUserAsync(It.IsAny<int>())).ReturnsAsync(new User());

               await _messageEntityService.GetChatMessages(requestUserId, chatUserId);

               _cacheService.Verify(x =>
                    x.SetInCacheAsync(
                         It.Is<IEnumerable<MessageEntity>>(x => x.First().MessageContent == "FirstMessage"),
                         It.IsAny<string>(), It.IsAny<CacheExpiryType>()));
          }

          [Fact]
          public async Task GetChatMessages_MessagesNotPresentInCache_CallsSetInCacheAsyncWithCorrectExpiryType()
          {
               var requestUserId = 1;
               var chatUserId = 2;
               var messages = new List<MessageEntity>()
               {
                    new() { Id = ObjectId.GenerateNewId(DateTime.UtcNow), MessageContent = "SecondMessage" },
                    new()
                    {
                         Id = ObjectId.GenerateNewId(DateTime.UtcNow.AddDays(-1)),
                         MessageContent = "FirstMessage"
                    },
               };
               _cacheService.Setup(x => x.GetFromCacheAsync<IEnumerable<MessageEntity>>(It.IsAny<string>()))
                    .ReturnsAsync(value: null);
               _messagesRepository.Setup(x => x.GetChatMessages(requestUserId, chatUserId)).ReturnsAsync(messages);
               _usersService.Setup(x => x.GetUserAsync(It.IsAny<int>())).ReturnsAsync(new User());

               await _messageEntityService.GetChatMessages(requestUserId, chatUserId);

               _cacheService.Verify(x => x.SetInCacheAsync(It.IsAny<IEnumerable<MessageEntity>>(), It.IsAny<string>(),
                    CacheExpiryType.TenMinutes));
          }

          [Fact]
          public async Task GetChatMessages_MessagesNotPresentInCache_ReturnOrderedMessagesFromRepository()
          {
               var requestUserId = 1;
               var chatUserId = 2;
               var messages = new List<MessageEntity>()
               {
                    new() { Id = ObjectId.GenerateNewId(DateTime.UtcNow), MessageContent = "SecondMessage" },
                    new()
                    {
                         Id = ObjectId.GenerateNewId(DateTime.UtcNow.AddDays(-1)),
                         MessageContent = "FirstMessage"
                    },
               };
               _cacheService.Setup(x => x.GetFromCacheAsync<IEnumerable<MessageEntity>>(It.IsAny<string>()))
                    .ReturnsAsync(value: null);
               _messagesRepository.Setup(x => x.GetChatMessages(requestUserId, chatUserId)).ReturnsAsync(messages);
               _usersService.Setup(x => x.GetUserAsync(It.IsAny<int>())).ReturnsAsync(new User());

               var result = await _messageEntityService.GetChatMessages(requestUserId, chatUserId);

               Assert.Equal("FirstMessage", result.First().MessageContent);
          }
     }
}