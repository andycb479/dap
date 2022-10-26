using Google.Protobuf;
using Grpc.Core;
using Grpc.Net.Client;
using Microsoft.Extensions.Configuration;
using Services.Core.Caching.Interface;
using Services.Core.ServiceDiscovery;
using Services.Infrastructure.Entity;
using Services.Infrastructure.Enums;

namespace ExternalServices.Services
{
     public class UsersService : IUsersService
     {
          private readonly IConsulService _consulService;
          private readonly ICacheService _cacheService;
          private readonly string _externalServiceName;
          private readonly string _currentServiceName;

          public UsersService(IConsulService consulService, ICacheService cacheService, IConfiguration configuration)
          {
               _consulService = consulService;
               _cacheService = cacheService;
               _externalServiceName = configuration.GetValue<string>("ExternalServicesNames:UsersService") ?? "UsersService";
               _currentServiceName = "ChatSessionService";
          }

          public async Task<User?> GetUserAsync(int userId)
          {
               var cacheKey = CreateChatCacheKey(userId);
               var user = await _cacheService.GetFromCacheAsync<User>(cacheKey);
               if (user is not null)
               {
                    return user;
               }

               user = await GetUserFromExternalService(userId);
               await _cacheService.SetInCacheAsync(user, cacheKey, CacheExpiryType.TwoMinutes);

               return user;
          }

          private string CreateChatCacheKey(int userId)
          {
               return _cacheService.CreateCacheKey(_currentServiceName, typeof(User), userId.ToString(), String.Empty);
          }

          private async Task<User?> GetUserFromExternalService(int userId)
          {
               var serviceUri = await _consulService.GetRequestUriAsync(_externalServiceName);

               using var channel = GrpcChannel.ForAddress(serviceUri);
               var client = new Users.UsersClient(channel);
               try
               {
                    var reply = await client.GetUserAsync(new UserIdRequest() { UserId = userId });
                    return reply;
               }
               catch (RpcException e)
               {
                    return null;
               }
          }
     }
}
