using Consul;

namespace Services.Core.ServiceDiscovery;

public interface IConsulService
{
     Task<Uri> GetRequestUriAsync(string serviceName);
}