using Consul;
using Microsoft.Extensions.Configuration;
using Services.Core.ServiceDiscovery;

namespace ChatSessionService.DAL.Service
{
     public class ConsulServiceDiscoveryService
     {
          private readonly ConsulClient _consulClient;

          public ConsulServiceDiscoveryService(IConfiguration configuration)
          {
               var serviceDiscoveryConfig = configuration.GetServiceConfig();
               _consulClient = new ConsulClient(c => c.Address = serviceDiscoveryConfig.DiscoveryAddress);
          }

          public IEnumerable<Uri> GetServiceUrisByName(string serviceName)
          {
               var services = _consulClient.Agent.Services().Result.Response;

               return (from service in services
                       let isRequiredService = service.Value.Service == serviceName
                       where isRequiredService
                       select new Uri($"{service.Value.Address}:{service.Value.Port}")).ToList();
          }
     }
}
