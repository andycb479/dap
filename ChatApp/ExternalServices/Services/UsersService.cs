using ExternalServices.Services.Base;
using ExternalServices.Users;
using Grpc.Core;
using Grpc.Net.Client;
using Microsoft.Extensions.Configuration;
using Services.Core.Caching.Interface;
using Services.Core.ServiceDiscovery;
using Services.Infrastructure.Enums;
using Services.Infrastructure.Extensions;

namespace ExternalServices.Services
{
     public class UsersService : ExternalServiceBase, IUsersService
     {
          public UsersService(IConsulService consulService, ICacheService cacheService, IConfiguration configuration)
               : base(consulService, cacheService, configuration, "UsersService")
          {
          }

          public async Task<User?> GetUserAsync(int userId)
          {
               var cacheKey = CreateChatCacheKey<User>(userId);
               var user = await CacheService.GetFromCacheAsync<User>(cacheKey);
               if (user is not null)
               {
                    return user;
               }

               user = await (GetUserFromExternalService(userId).TimeoutAfter(MaxTimeout));
               await CacheService.SetInCacheAsync(user, cacheKey, CacheExpiryType.TwoMinutes);
               return user;
          }

          public async Task DeleteUserAsync(int userId)
          {
               var client = await GetRpcGetClient();
          }

          public async Task RollbackUserDeleteAsync(int userId)
          {
               var client = await GetRpcGetClient();
          }

          private async Task<User?> GetUserFromExternalService(int userId)
          {
               var client = await GetRpcGetClient();
               try
               {
                    var reply = await client.GetUserAsync(new UserIdRequest() { UserId = userId });
                    return reply;
               }
               catch (RpcException e) when (e.StatusCode == StatusCode.Unavailable)
               {
                    throw new Exception("No users service instance is available!");
               }
               catch (RpcException e) when (e.StatusCode == StatusCode.Unknown)
               {
                    return null;
               }
          }

          private async Task<Users.Users.UsersClient> GetRpcGetClient()
          {
               var serviceUri = await ConsulService.GetRequestUriAsync(ExternalServiceName);

               using var channel = GrpcChannel.ForAddress(serviceUri);
               return new Users.Users.UsersClient(channel);
          }
     }
}
