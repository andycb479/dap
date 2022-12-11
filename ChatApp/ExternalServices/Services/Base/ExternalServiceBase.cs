using Microsoft.Extensions.Configuration;
using Services.Core.Caching.Interface;
using Services.Core.ServiceDiscovery;

namespace ExternalServices.Services.Base
{
     public abstract class ExternalServiceBase : IExternalServiceBase
     {
          protected readonly IConsulService ConsulService;
          protected readonly ICacheService CacheService;
          private readonly IConfiguration _configuration;
          protected readonly string ExternalServiceName;

          protected ExternalServiceBase(IConsulService consulService, ICacheService cacheService, IConfiguration configuration, string externalServiceName)
          {
               ConsulService = consulService;
               CacheService = cacheService;
               _configuration = configuration;
               ExternalServiceName = externalServiceName;
          }

          public string CreateChatCacheKey<T>(string clientIdentifier, int userId)
          {
               return CacheService.CreateCacheKey(clientIdentifier, typeof(T), userId.ToString(), String.Empty);
          }
     }
}
