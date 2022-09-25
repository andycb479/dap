using ChatSessionService.DAL.Interface;
using ChatSessionService.ExternalServices.Interface;
using Grpc.Net.Client;
using MessageDistributionService;
using Services.Infrastructure.Entity;
using Services.Infrastructure.Enums;

namespace ChatSessionService.ExternalServices
{
     public class DistributionService : IDistributionService
    {
         private readonly IMessagesRepository _messagesRepository;

         public DistributionService(IMessagesRepository messagesRepository)
         {
              _messagesRepository = messagesRepository;
         }

        public async void RedirectMessage(MessageEntity message)
        {
             try
             {
                  var channel = GrpcChannel.ForAddress("http://localhost:5287");
                  var client = new MessageDistribution.MessageDistributionClient(channel);

                  var request = new RedirectMessageRequest
                  {
                       MessageContent = message.MessageContent,
                       FromUserId = message.FromUserId,
                       ToUserId = message.ToUserId,
                       Date = message.CreatedAt.ToShortDateString()
                  };

                  int timeout = 1000;
                  var task = client.RedirectMessageAsync(request).ResponseAsync;
                  if (await Task.WhenAny(task, Task.Delay(timeout)) == task)
                  {
                       message.MessageStatus = MessageStatus.Delivered;
                       await _messagesRepository.ReplaceOneAsync(message);
                  } 

             }
             catch (Exception e)
             {
                  Console.WriteLine(e);
             }
        }
    }
}
