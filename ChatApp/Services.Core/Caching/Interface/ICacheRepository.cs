using Microsoft.Extensions.Caching.Distributed;

namespace Services.Core.Caching.Interface;

public interface ICacheRepository
{
    Task<string> GetStringAsync(string key);
    Task RemoveAsync(string key);
    Task SetStringAsync(string key, string value, DistributedCacheEntryOptions options);
}