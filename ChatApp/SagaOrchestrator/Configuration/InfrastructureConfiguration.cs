using Services.Core.ServiceDiscovery;

namespace SagaOrchestrator.Configuration;

public static class InfrastructureConfiguration
{
     public static void ConfigureInfrastructure(this IServiceCollection services, IConfiguration configuration)
     {
          services.AddScoped<IConsulService, ConsulService>();
     }
}    