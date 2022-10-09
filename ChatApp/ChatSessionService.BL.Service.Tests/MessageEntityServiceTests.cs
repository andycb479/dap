using ChatSessionService.BL.Interface;
using ChatSessionService.DAL.Interface;
using Microsoft.Extensions.Configuration;
using Moq;
using Services.Core.Caching.Interface;
using Services.Infrastructure;
using Services.Infrastructure.Entity;
using Xunit;

namespace ChatSessionService.BL.Service.Tests
{
     public class MessageEntityServiceTests
     {
          private readonly Mock<IMessagesRepository> _messagesRepository;
          private readonly Mock<ICacheService> _cacheService;
          private readonly IMessageEntityService _messageEntityService;

          public MessageEntityServiceTests()
          {
               _cacheService = new Mock<ICacheService>();
               _messagesRepository = new Mock<IMessagesRepository>();

               Mock<IConfiguration> configuration = new Mock<IConfiguration>();
               configuration.Setup(c => c.GetSection(It.IsAny<String>())).Returns(new Mock<IConfigurationSection>().Object);

               TestClassSetup();

               _messageEntityService = new MessageEntityService(_messagesRepository.Object, _cacheService.Object, configuration.Object);
          }

          private void TestClassSetup()
          {

          }

          [Fact]
          public async Task Insert_FromUserIdIsNegative_ThrowsValidationException()
          {
               var message = new MessageEntity() { FromUserId = -1 };

               var result = await Assert.ThrowsAsync<ValidationException>(async () => await _messageEntityService.Insert(message));

               Assert.Equal("Invalid sender.", result.Message);
          }

          [Fact]
          public async Task Insert_ToUserIdIsNegative_ThrowsValidationException()
          {
               var message = new MessageEntity() { ToUserId = -1 };

               var result = await Assert.ThrowsAsync<ValidationException>(async () => await _messageEntityService.Insert(message));

               Assert.Equal("Invalid receiver.", result.Message);
          }

          [Fact]
          public async Task Insert_MessageContentIsNull_ThrowsValidationException()
          {
               var message = new MessageEntity() { ToUserId = 1, FromUserId = 2, MessageContent = null};

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
     }
}