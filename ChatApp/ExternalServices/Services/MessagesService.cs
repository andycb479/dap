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

          public async Task DeleteUserMessages(int userId)
          {
               var client = await GetRpcGetClient();
               try
               {
                    var reply = await client.DeleteUserChatsAsync(new UserIdRequest() { UserId = 2 });
               }
               catch (RpcException e) when (e.StatusCode == StatusCode.Unavailable)
               {
                    throw new Exception("No users service instance is available!");
               }
               catch (RpcException e) when (e.StatusCode == StatusCode.Unknown)
               {
               }
          }

          public async Task RollbackDeleteUserMessages(int userId)
          {
               var client = await GetRpcGetClient();
          }

          private async Task<Messages.Messages.MessagesClient> GetRpcGetClient()
          {
               //var serviceUri = await _consulService.GetRequestUriAsync(_externalServiceName);
               var serviceUri = new UriBuilder() { Host = "localhost", Port = 5103 }.Uri;

               using var channel = GrpcChannel.ForAddress(serviceUri);
               return new Messages.Messages.MessagesClient(channel);
          }
     }
}
