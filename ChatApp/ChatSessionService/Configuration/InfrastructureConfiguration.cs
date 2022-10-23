using Services.Core.ServiceDiscovery;

namespace ChatSessionService.Configuration;

public static class InfrastructureConfiguration
{
     public static void ConfigureInfrastructure(this IServiceCollection services, IConfiguration configuration)
     {
          services.AddScoped<IConsulService, ConsulService>();
     }
}