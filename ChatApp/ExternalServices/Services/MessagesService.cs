using ExternalServices.Messages;
using ExternalServices.Services.Base;
using Grpc.Core;
using Grpc.Net.Client;
using Microsoft.Extensions.Configuration;
using Services.Core.Caching.Interface;
using Services.Core.ServiceDiscovery;

namespace ExternalServices.Services
{
     public class MessagesService : ExternalServiceBase, IMessagesService
     {
          public MessagesService(IConsulService consulService, ICacheService cacheService, IConfiguration configuration)
               : base(consulService, cacheService, configuration, "ChatSessionService")
          {
          }

          public async Task DeleteUserMessages(string clientIdentifier, int userId)
          {
               var serviceUri = await ConsulService.GetRequestUriAsync(ExternalServiceName);

               using var channel = GrpcChannel.ForAddress(serviceUri);
               var client = new Messages.Messages.MessagesClient(channel);

               try
               {
                   await client.DeleteUserChatsAsync(new UserIdRequest() { UserId = userId });
               }
               catch (RpcException e) when (e.StatusCode == StatusCode.Unavailable)
               {
                    throw new Exception("No Chat Session Service is available!");
               }
          }
     }
}
