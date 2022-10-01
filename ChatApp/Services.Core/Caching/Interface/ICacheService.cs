using Services.Infrastructure.Enums;

namespace Services.Core.Caching.Interface;

public interface ICacheService
{
     Task<TCachedType?> GetFromCacheAsync<TCachedType>(string key);
     Task SetInCacheAsync<TCachedType>(TCachedType objectToCache, string key, CacheExpiryType cacheExpiryType);
     string CreateCacheKey(string serviceName, Type type, string id, string customSuffix);
     Task RemoveAsync(string key);
}