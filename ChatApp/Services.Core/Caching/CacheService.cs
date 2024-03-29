﻿using Microsoft.Extensions.Caching.Distributed;
using Services.Core.Caching.Interface;
using Services.Infrastructure.Attributes;
using Services.Infrastructure.Enums;
using System.Text;
using Newtonsoft.Json;

namespace Services.Core.Caching
{
     public class CacheService : ICacheService
     {
          private readonly ICacheRepository _cacheStore;

          private readonly JsonSerializerSettings _serializerSettings;

          public CacheService(ICacheRepository cacheStore)
          {
               _cacheStore = cacheStore;

               _serializerSettings = new JsonSerializerSettings() { ReferenceLoopHandling = ReferenceLoopHandling.Ignore};
          }

          public async Task<TCachedType?> GetFromCacheAsync<TCachedType>(string key)
          {
               var cacheStoreResult = await _cacheStore.GetStringAsync(key);
               if (string.IsNullOrEmpty(cacheStoreResult))
               {
                    return default;
               }

               try
               {
                    return JsonConvert.DeserializeObject<TCachedType>(cacheStoreResult);
               }
               catch (Exception e)
               {
                    return default;
               }
          }

          public async Task SetInCacheAsync<TCachedType>(TCachedType objectToCache, string key, CacheExpiryType cacheExpiryType)
          {
               string cacheString = FormatObjectForCaching(objectToCache, key);

               var options = new DistributedCacheEntryOptions()
               { AbsoluteExpiration = new DateTimeOffset(DateTime.Now.AddMinutes((int)cacheExpiryType)) };

               await _cacheStore.SetStringAsync(key, cacheString, options);
          }

          public string CreateCacheKey(string serviceName, Type type, string id, string customSuffix)
          {
               StringBuilder sb = new StringBuilder(serviceName.ToLower());

               var entityType = type.FullName.Split(',')[0];
               entityType = entityType.Contains("[[") ? $"{entityType}]]" : entityType;
               sb.Append($"-{entityType}");

               sb.Append($"-Id-{id}");

               if (!string.IsNullOrEmpty(customSuffix))
               {
                    sb.Append($"-{customSuffix}");
               }

               return sb.ToString();
          }

          public async Task RemoveAsync(string key)
          {
               await _cacheStore.RemoveAsync(key);
          }

          private string FormatObjectForCaching<TCachedType>(TCachedType objectToCache, string key)
          {
               var cacheString = JsonConvert.SerializeObject(objectToCache, Formatting.None, _serializerSettings);
               return cacheString;
          }
     }
}
