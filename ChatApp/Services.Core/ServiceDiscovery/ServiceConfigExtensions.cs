using Microsoft.Extensions.Configuration;

namespace Services.Core.ServiceDiscovery
{
     public static class ServiceConfigExtensions
     {
          public static ServiceConfig GetServiceConfig(this IConfiguration configuration)
          {
               if (configuration == null)
               {
                    throw new ArgumentNullException(nameof(configuration));
               }

               var serviceConfig = new ServiceConfig
               {
                    Id = configuration.GetValue<string>("ServiceConfig:Id") ?? "ChatSessionService-9100",
                    Name = configuration.GetValue<string>("ServiceConfig:Name") ?? "ChatSessionService",
                    Address = configuration.GetValue<string>("ServiceConfig:Address") ?? "localhost",
                    Port = configuration.GetValue<int>("ServiceConfig:Port") == 0 ? 5103 : configuration.GetValue<int>("ServiceConfig:Port"),
                    DiscoveryAddress = configuration.GetValue<Uri>("ServiceConfig:DiscoveryAddress") ?? new Uri("http://localhost:8500"),
               };

               return serviceConfig;
          }
     }
}