using ChatSessionService.BL.Interface;
using ChatSessionService.ExternalServices;
using ChatSessionService.Infrastructure.Enums;
using Grpc.Core;

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
               await _messagesService.InsertMessage(new Infrastructure.Entity.Message
               {
                    FromUserId = request.FromUserId,
                    ToUserId = request.ToUserId,
                    MessageContent = request.MessageContent,
                    MessageStatus = MessageStatus.Sent
               });

               await _distributionService.RedirectMessage(new Message
               {
                    FromUserId = request.FromUserId,
                    ToUserId = request.ToUserId,
                    MessageContent = request.MessageContent,
                    MessageStatus = (int)MessageStatus.Sent,
                    Date = DateTime.UtcNow.ToShortTimeString()
               });

               return await Task.FromResult(new GenericReply { Response = "Message received." + request.MessageContent });
          }

          public override async Task GetChatMessages(ChatRequest request, IServerStreamWriter<Message> responseStream, ServerCallContext context){
               var messages =  await _messagesService.GetChatMessages(request.RequestUserId, request.ChatUserId);
               var mappedMessages = MapProtoMessageToAppMessage(messages);

               foreach (var message in mappedMessages)
               {
                    await responseStream.WriteAsync(message);
               }
          }

          private IEnumerable<Message> MapProtoMessageToAppMessage (IEnumerable<Infrastructure.Entity.Message> messages)
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
