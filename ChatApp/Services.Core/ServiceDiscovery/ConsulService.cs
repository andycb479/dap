using Consul;
using Microsoft.Extensions.Configuration;

namespace Services.Core.ServiceDiscovery
{
     public class ConsulService
     {
          private readonly ConsulClient _consulClient;

          public ConsulService(IConfiguration configuration)
          {
               var serviceDiscoveryConfig = configuration.GetServiceConfig();
               _consulClient = new ConsulClient(c => c.Address = serviceDiscoveryConfig.DiscoveryAddress);
          }

          private async Task<Uri> GetRequestUriAsync(string serviceName)
          {
               //Get all services registered on Consul
               var allRegisteredServices = await _consulClient.Agent.Services();

               //Get all instance of the service went to send a request to
               var registeredServices = allRegisteredServices.Response?.Where(s => s.Value.Service.Equals(serviceName, StringComparison.OrdinalIgnoreCase)).Select(x => x.Value).ToList();

               //Get a random instance of the service
               var service = GetRandomInstance(registeredServices, serviceName);

               if (service == null)
               {
                    throw new Exception($"Consul service: '{serviceName}' was not found.");
               }

               var uriBuilder = new UriBuilder()
               {
                    Host = service.Address,
                    Port = service.Port
               };

               return uriBuilder.Uri;
          }

          private AgentService GetRandomInstance(IList<AgentService> services, string serviceName)
          {
               Random random = new Random();

               var serviceToUse = services[random.Next(0, services.Count)];

               return serviceToUse;
          }
     }
}
