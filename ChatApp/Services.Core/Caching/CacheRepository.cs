using Microsoft.Extensions.Caching.Distributed;
using Services.Core.Caching.Interface;

namespace Services.Core.Caching
{
     public class CacheRepository : ICacheRepository
     {
          private readonly IDistributedCache _cache;

          public CacheRepository(IDistributedCache cache)
          {
               _cache = cache;
          }

          public async Task<string> GetStringAsync(string key)
          {
               return await _cache.GetStringAsync(key);
          }

          public async Task RemoveAsync(string key)
          {
               await _cache.RemoveAsync(key);
          }

          public async Task SetStringAsync(string key, string value, DistributedCacheEntryOptions options)
          {
               await _cache.SetStringAsync(key, value, options);
          }
     }
}
