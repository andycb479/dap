using ChatSessionService.BL.Interface;
using ChatSessionService.ExternalServices.Interface;
using Grpc.Core;
using Services.Infrastructure;
using Services.Infrastructure.Enums;
using Entity = Services.Infrastructure.Entity;

namespace ChatSessionService.Services
{
     public class MessagesService : Messages.MessagesBase
     {
          private readonly IMessagesService _messagesService;
          private readonly IDistributionService _distributionService;

          public MessagesService(IMessagesService messagesService, IDistributionService distributionService)
          {
               _messagesService = messagesService;
               _distributionService = distributionService;
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

                    await _messagesService.InsertMessage(messageEntity);

                    _distributionService.RedirectMessage(messageEntity);

                    return await Task.FromResult(new GenericReply { Response = "Message sent." });
               }
               catch (ValidationException e)
               {
                    return await Task.FromResult(new GenericReply { Response = e.Message });
               }
               catch (Exception e)
               {
                    return await Task.FromResult(new GenericReply { Response = "Message sent failed." });
               }

          }

          public override async Task GetChatMessages(ChatRequest request, IServerStreamWriter<Message> responseStream, ServerCallContext context){
               var messages =  await _messagesService.GetChatMessages(request.RequestUserId, request.ChatUserId);
               var mappedMessages = MapProtoMessageToAppMessage(messages);

               foreach (var message in mappedMessages)
               {
                    await responseStream.WriteAsync(message);
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
