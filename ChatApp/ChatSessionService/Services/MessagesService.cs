using ChatSessionService.BL.Interface;
using ChatSessionService.Infrastructure.Enums;
using Grpc.Core;

namespace ChatSessionService.Services
{
     public class MessagesService : Messages.MessagesBase
     {
          private readonly IMessagesService _messagesService;

          public MessagesService(IMessagesService messagesService)
          {
               _messagesService = messagesService;
          }
          public override async Task<SendMessageReply> SendMessage(SendMessageRequest request, ServerCallContext context)
          {
               await _messagesService.InsertMessage(new Infrastructure.Entity.Message
               {
                    FromUserId = request.FromUserId,
                    ToUserId = request.ToUserId,
                    MessageContent = request.MessageContent,
                    MessageStatus = MessageStatus.Sent
               });

               return await Task.FromResult(new SendMessageReply { Response = "Message received." + request.MessageContent });
          }

          public override async Task GetChatMessages(GetChatMessagesRequest request, IServerStreamWriter<Message> responseStream, ServerCallContext context){
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
