using Grpc.Net.Client;
using MessageDistributionService;

namespace ChatSessionService.ExternalServices
{
     public class DistributionService : IDistributionService
    {
        public DistributionService()
        {
        }

        public async Task RedirectMessage(Message message)
        {
            var channel = GrpcChannel.ForAddress("http://localhost:5287");
            var client = new MessageDistribution.MessageDistributionClient(channel);

            var request = new RedirectMessageRequest
            {
                MessageContent = message.MessageContent,
                FromUserId = message.FromUserId,
                ToUserId = message.ToUserId,
                Date = message.Date
            };

            var response = await client.RedirectMessageAsync(request);

            Console.WriteLine(response.Status);
        }
    }
}
