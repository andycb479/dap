using OpenSleigh.Core.DependencyInjection;
using OpenSleigh.Persistence.InMemory;
using OpenSleigh.Persistence.Mongo;
using SagaOrchestrator.Sagas;

namespace SagaOrchestrator.Configuration;

public static class SagaConfiguration
{
     public static void ConfigureSagas(this IServiceCollection services, IConfiguration configuration)
     {
          services.AddOpenSleigh(cfg =>
          {
               InMemoryBusConfiguratorExtensions.UseInMemoryTransport(cfg);

               var mongoCfg =
                    new MongoConfiguration(configuration.GetValue<string>("MongoDbSettings:ConnectionString"),
                         configuration.GetValue<string>("MongoDbSettings:DatabaseName"));
               MongoBusConfiguratorExtensions.UseMongoPersistence(cfg, mongoCfg);

               InMemorySagaConfiguratorExtensions.UseInMemoryTransport<DeleteUserSaga, DeleteUserSagaState>(cfg.AddSaga<DeleteUserSaga, DeleteUserSagaState>()
                         .UseStateFactory<DeleteUsersServiceUser>(msg => new DeleteUserSagaState(msg.CorrelationId)));
          });
     }
}