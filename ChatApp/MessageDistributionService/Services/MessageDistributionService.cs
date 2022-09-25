using Grpc.Core;
using Services.Infrastructure.Enums;

namespace MessageDistributionService.Services
{
     public class MessageDistributionService : MessageDistribution.MessageDistributionBase
     {
          public override Task<RedirectMessageReply> RedirectMessage(RedirectMessageRequest request, ServerCallContext context)
          {
               return Task.FromResult(new RedirectMessageReply()
               {
                    DistributionStatus = (int) DistributionStatus.Delivered
               });
          }
     }
}
