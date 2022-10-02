using ChatSessionService.BL.Interface;
using ChatSessionService.BL.Service;

namespace ChatSessionService.Configuration;

public static class BlConfiguration
{
     public static void ConfigureBusinessLayer(this IServiceCollection services, IConfiguration configuration)
     {
          services.AddScoped<IMessageEntityService, MessageEntityService>();
     }
}