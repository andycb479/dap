using ChatSessionService.BL.Interface;
using Grpc.Core;
using Services.Infrastructure;
using Services.Infrastructure.Enums;
using Entity = Services.Infrastructure.Entity;

namespace ChatSessionService.Services
{
     public class MessagesService : Messages.MessagesBase
     {
          private readonly IMessageEntityService _messagesService;
          private readonly ILogger _logger;

          public MessagesService(IMessageEntityService messagesService, ILogger<Message> logger)
          {
               _messagesService = messagesService;
               _logger = logger;
          }

          public override async Task<GenericReply> ChangeMessagesForChatToSeen(ChatRequest request, ServerCallContext context)
          {
               await _messagesService.ChangeMessagesForChatToSeen(request.RequestUserId, request.ChatUserId);

               return await Task.FromResult(new GenericReply { Response = "Updated" });
          }

          public override async Task<GenericReply> SendMessage(SendMessageRequest request, ServerCallContext context)
          {
               try
               {
                    var messageEntity = new Entity.MessageEntity
                    {
                         FromUserId = request.FromUserId,
                         ToUserId = request.ToUserId,
                         MessageContent = request.MessageContent,
                         MessageStatus = MessageStatus.Sent
                    };

                    await _messagesService.Insert(messageEntity);

                    _logger.LogInformation("A message was sent from {FromUserId} to {ToUserId}", request.FromUserId,
                         request.ToUserId);

                    return await Task.FromResult(new GenericReply { Response = "Message sent." });
               }
               catch (ValidationException e)
               {
                    _logger.LogError(
                         "A validation error occurred when sending a message from {FromUserId} to {ToUserId}",
                         request.FromUserId, request.ToUserId);

                    return await Task.FromResult(new GenericReply { Response = e.Message });
               }
               catch (Exception e)
               {
                    return await Task.FromResult(new GenericReply { Response = "Message sent failed." });
               }
          }

          public override async Task GetChatMessages(ChatRequest request, IServerStreamWriter<Message> responseStream, ServerCallContext context)
          {
               try
               {
                    var messages = await _messagesService.GetChatMessages(request.RequestUserId, request.ChatUserId);
                    var mappedMessages = MapProtoMessageToAppMessage(messages);

                    foreach (var message in mappedMessages)
                    {
                         await responseStream.WriteAsync(message);
                    }

                    _logger.LogInformation("Chat for users {x} and {y} send.", request.RequestUserId, request.ChatUserId);
               }
               catch (ValidationException e)
               {
                    _logger.LogError(
                         "A validation error occurred when receiving chat for {RequestUserId} and {ChatUserId}. Message: {Message}",
                         request.RequestUserId, request.ChatUserId, e.Message);
               }
               catch (Exception e)
               {
                   _logger.LogError(e.Message);
               }
          }

          private IEnumerable<Message> MapProtoMessageToAppMessage (IEnumerable<global::Services.Infrastructure.Entity.MessageEntity> messages)
          {
               return messages.Select(message => new Message
                    {
                         MessageStatus = (int)message.MessageStatus,
                         FromUserId = message.FromUserId,
                         ToUserId = message.ToUserId,
                         Date = message.CreatedAt.ToShortDateString(),
                         MessageContent = message.MessageContent
                    })
                    .ToList();
          }
     }
}
