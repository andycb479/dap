using Services.Core.Caching;
using Services.Core.Caching.Interface;

namespace ChatSessionService.Configuration;

public static class CacheConfiguration
{
     public static void ConfigureRedisCache(this IServiceCollection services, IConfiguration configuration)
     {

          services.AddStackExchangeRedisCache(options =>
          {
               // options.Configuration = configuration.GetConnectionString("RedisConnectionString");
               options.Configuration = "localhost";
               options.InstanceName = "Cache";
          });
          services.AddScoped<ICacheRepository, CacheRepository>();
          services.AddScoped<ICacheService, CacheService>();
     }
}