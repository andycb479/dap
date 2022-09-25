using ChatSessionService.BL.Interface;
using ChatSessionService.BL.Service;
using ChatSessionService.ExternalServices;
using ChatSessionService.ExternalServices.Interface;

namespace ChatSessionService.Configuration;

public static class BlConfiguration
{
     public static void ConfigureBusinessLayer(this IServiceCollection services, IConfiguration configuration)
     {
          services.AddScoped<IMessagesService, MessagesService>();
          services.AddScoped<IDistributionService, DistributionService>();
     }
}