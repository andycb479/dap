using ChatSessionService.ExternalServices.Interface;
using Grpc.Net.Client;
using MessageDistributionService;
using Services.Infrastructure.Entity;

namespace ChatSessionService.ExternalServices
{
     public class DistributionService : IDistributionService
    {
        public DistributionService()
        {
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
                       Console.WriteLine($"Delivered {message.Id}");
                  } 

             }
             catch (Exception e)
             {
                  Console.WriteLine(e);
             }
        }
    }
}
