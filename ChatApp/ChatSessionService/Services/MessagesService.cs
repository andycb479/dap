using ChatSessionService.BL.Interface;
using Grpc.Core;
using Services.Infrastructure.Enums;
using Services.Infrastructure.Exceptions;
using Entity = Services.Infrastructure.Entity;

namespace ChatSessionService.Services
{
    public class MessagesService : Messages.MessagesBase
     {
          private readonly IMessageEntityService _messagesService;
          private readonly ILogger _logger;

          public MessagesService(IMessageEntityService messagesService, ILogger<MessagesService> logger)
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

                    _logger.LogInformation("A message was sent from UserId {FromUserId} to UserId {ToUserId}",
                         request.FromUserId,
                         request.ToUserId);

                    return await Task.FromResult(new GenericReply { Response = "Message sent." });
               }
               catch (ValidationException e)
               {
                    _logger.LogError(
                         "A validation error occurred when sending a message from {FromUserId} to {ToUserId}. {validationMessage}",
                         request.FromUserId, request.ToUserId, e.Message);

                    throw new RpcException(new Status(StatusCode.InvalidArgument, e.Message));
               }
               catch (TimeoutException)
               {
                    _logger.LogError("One of the tasks timed out!");

                    throw new RpcException(new Status(StatusCode.DeadlineExceeded,
                         Newtonsoft.Json.JsonConvert.SerializeObject(new { Code = 408 })));
               }
               catch (Exception e)
               {
                    _logger.LogError("Error:{message}", e.Message);

                    throw new RpcException(new Status(StatusCode.Internal, "Message sent failed."));
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

                    throw new RpcException(new Status(StatusCode.InvalidArgument, "Invalid chat arguments."));
               }
               catch (TimeoutException)
               {
                    _logger.LogError("One of the tasks timed out!");

                    throw new RpcException(new Status(StatusCode.DeadlineExceeded,
                         Newtonsoft.Json.JsonConvert.SerializeObject(new { Code = 408 })));
               }
               catch (Exception e)
               {
                    _logger.LogError("Error:{message}", e.Message);

                    throw new RpcException(new Status(StatusCode.Internal, "Chat retrieval failed."));
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
