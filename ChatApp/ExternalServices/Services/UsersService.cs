using Grpc.Net.Client;
using Microsoft.Extensions.Configuration;
using Services.Core.ServiceDiscovery;

namespace ExternalServices.Services
{
     public class UsersService : IUsersService
     {
          private readonly IConsulService _consulService;
          private readonly string _serviceName;
          public UsersService(IConsulService consulService, IConfiguration configuration)
          {
               _consulService = consulService;
               _serviceName = configuration.GetValue<string>("ExternalServicesNames:UsersService") ?? "UsersService";
          }

          public async Task<User> GetUserAsync(int userId)
          {
               var serviceUri = await _consulService.GetRequestUriAsync(_serviceName);

               using var channel = GrpcChannel.ForAddress(serviceUri);
               var client = new Users.UsersClient(channel);
               var reply = await client.GetUserAsync(new UserIdRequest() { UserId = userId });

               return reply;
          }
     }
}
