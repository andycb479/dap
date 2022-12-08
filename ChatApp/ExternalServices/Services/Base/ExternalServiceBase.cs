using Microsoft.Extensions.Configuration;
using Services.Core.Caching.Interface;
using Services.Core.ServiceDiscovery;

namespace ExternalServices.Services.Base
{
     public abstract class ExternalServiceBase : IExternalServiceBase
     {
          protected readonly IConsulService ConsulService;
          protected readonly ICacheService CacheService;
          protected readonly string ExternalServiceName;
          protected readonly string CurrentServiceName;
          protected readonly int MaxTimeout;

          protected ExternalServiceBase(IConsulService consulService, ICacheService cacheService, IConfiguration configuration, string currentServiceName)
          {
               ConsulService = consulService;
               CacheService = cacheService;
               CurrentServiceName = currentServiceName;
               MaxTimeout = configuration.GetValue<int?>("ServiceConfig:MaxTimeoutUsersService") ?? configuration.GetValue<int>("TaskTimeout:MaxTimeout");
               ExternalServiceName = configuration.GetValue<string>("ExternalServicesNames:UsersService") ?? "UsersService";
          }

          public string CreateChatCacheKey<T>(int userId)
          {
               return CacheService.CreateCacheKey(CurrentServiceName, typeof(T), userId.ToString(), String.Empty);
          }
     }
}
