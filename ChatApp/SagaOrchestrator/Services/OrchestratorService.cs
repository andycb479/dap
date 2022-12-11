using Grpc.Core;
using OpenSleigh.Core.Messaging;
using SagaOrchestrator.Sagas;

namespace SagaOrchestrator.Services
{
     public class OrchestratorService : Orchestrator.OrchestratorBase
     {
          private readonly Serilog.ILogger _logger;
          private readonly IMessageBus _orchestratorMessageBus;

          public OrchestratorService(Serilog.ILogger logger, IMessageBus orchestratorMessageBus)
          {
               _logger = logger;
               _orchestratorMessageBus = orchestratorMessageBus;
          }

          public override async Task<GenericReply> DeleteUser(DeleteUserRequest request, ServerCallContext context)
          {
               _logger.Information("Starting Deletion User Transaction");

               await _orchestratorMessageBus.PublishAsync(new DeleteUsersServiceUser(Guid.NewGuid(), Guid.NewGuid(),
                    request.UserId));

               return await Task.FromResult(new GenericReply { Response = "Accepted" });
          }
     }
}
