using Grpc.Core;

namespace MessageDistributionService.Services
{
     public class MessageDistributionService : MessageDistribution.MessageDistributionBase
     {
          public override Task<RedirectMessageReply> RedirectMessage(RedirectMessageRequest request, ServerCallContext context)
          {
               Console.WriteLine(request.MessageContent);

               return Task.FromResult(new RedirectMessageReply()
               {
                    Status = "Awaiting"
               });
          }
     }
}
