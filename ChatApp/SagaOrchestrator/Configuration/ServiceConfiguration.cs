using ExternalServices.Services;

namespace SagaOrchestrator.Configuration;

public static class ServiceConfiguration
{
     public static void ConfigureServices(this IServiceCollection services, IConfiguration configuration)
     {
          services.AddScoped<IMessagesService, MessagesService>();
          services.AddScoped<IUsersService, UsersService>();
     }
}