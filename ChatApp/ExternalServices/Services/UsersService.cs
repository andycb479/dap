using ExternalServices.Services.Base;
using Grpc.Core;
using Grpc.Net.Client;
using Microsoft.Extensions.Configuration;
using Services.Core.Caching.Interface;
using Services.Core.ServiceDiscovery;
using Services.Infrastructure.Enums;
using Services.Infrastructure.Extensions;
using Users;

namespace ExternalServices.Services
{
     public class UsersService : ExternalServiceBase, IUsersService
     {
          public int MaxTimeout { get; set; }


          public UsersService(IConsulService consulService, ICacheService cacheService, IConfiguration configuration)
               : base(consulService, cacheService, configuration, "UsersService")
          {
               MaxTimeout = configuration.GetValue<int?>("ServiceConfig:MaxTimeoutUsersService") ?? configuration.GetValue<int>("TaskTimeout:MaxTimeout");
          }

          public async Task<User?> GetUserAsync(string clientIdentifier, int userId)
          {
               var cacheKey = CreateChatCacheKey<User>(clientIdentifier, userId);
               var user = await CacheService.GetFromCacheAsync<User>(cacheKey);
               if (user is not null)
               {
                    return user;
               }

               user = await (GetUserFromExternalService(userId).TimeoutAfter(MaxTimeout));
               await CacheService.SetInCacheAsync(user, cacheKey, CacheExpiryType.TenMinutes);
               return user;
          }

          public async Task DeleteUserAsync(string clientIdentifier, int userId, Guid transactionId)
          {
               var serviceUri = await ConsulService.GetRequestUriAsync(ExternalServiceName);

               using var channel = GrpcChannel.ForAddress(serviceUri);
               var client = new Users.Users.UsersClient(channel);

               await client.DeleteUserAsync(new UserAndTransactionIdRequest()
               {
                    UserId = userId, TransactionId = transactionId.ToString()
               });
          }

          public async Task RollbackUserDeleteAsync(string clientIdentifier, int userId, Guid transactionId)
          {
               var serviceUri = await ConsulService.GetRequestUriAsync(ExternalServiceName);

               using var channel = GrpcChannel.ForAddress(serviceUri);
               var client = new Users.Users.UsersClient(channel);

               await client.RollbackUserDeletionAsync(new UserAndTransactionIdRequest()
               {
                    UserId = userId, TransactionId = transactionId.ToString()
               });
          }

          private async Task<User?> GetUserFromExternalService(int userId)
          {
               var serviceUri = await ConsulService.GetRequestUriAsync(ExternalServiceName);
               try
               {
                    using var channel = GrpcChannel.ForAddress(serviceUri);
                    var client = new Users.Users.UsersClient(channel);
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
     }
}
