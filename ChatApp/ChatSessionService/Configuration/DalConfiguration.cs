using ChatSessionService.DAL.Interface;
using ChatSessionService.DAL.Service;
using ChatSessionService.Infrastructure.Configurations;
using ChatSesssionService.DAL.Service;
using Microsoft.Extensions.Options;

namespace ChatSessionService.Configuration
{
     public static class DalConfiguration
     {
          public static void ConfigureDataLayer(this IServiceCollection services, IConfiguration configuration)
          {
               services.Configure<MongoDbSettings>(configuration.GetSection("MongoDbSettings"));
               services.AddSingleton<IMongoDbSettings>(serviceProvider =>
                    serviceProvider.GetRequiredService<IOptions<MongoDbSettings>>().Value);

               services.AddScoped(typeof(IMongoRepository<>), typeof(MongoRepository<>));

               services.AddScoped<IMessagesRepository, MessagesRepository>();
          }
     }
}
