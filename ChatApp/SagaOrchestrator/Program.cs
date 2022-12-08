using ExternalServices.Services;
using OpenSleigh.Core.DependencyInjection;
using OpenSleigh.Persistence.InMemory;
using OpenSleigh.Persistence.Mongo;
using SagaOrchestrator.Sagas;

var builder = WebApplication.CreateBuilder(args);

// Additional configuration is required to successfully run gRPC on macOS.
// For instructions on how to configure Kestrel and gRPC clients on macOS, visit https://go.microsoft.com/fwlink/?linkid=2099682

// Add services to the container.
builder.Services.AddGrpc();
builder.Services.AddOpenSleigh(cfg =>
{
     cfg.UseInMemoryTransport();

     var mongoCfg = new MongoConfiguration("mongodb+srv://andy007:sWjQki2BWK2wdbZ3@cluster0.8nibr.mongodb.net/?retryWrites=true&w=majority",
          "dap");
     cfg.UseMongoPersistence(mongoCfg);

     cfg.AddSaga<DeleteUserSaga, DeleteUserSagaState>()
          .UseStateFactory<DeleteUsersServiceUser>(msg => new DeleteUserSagaState(msg.CorrelationId))
          .UseInMemoryTransport();
});

builder.Services.AddScoped<IMessagesService, MessagesService>();
builder.Services.AddScoped<IUsersService, UsersService>();

var app = builder.Build();

// Configure the HTTP request pipeline.
// app.MapGrpcService<GreeterService>();
app.MapGet("/", () => "Communication with gRPC endpoints must be made through a gRPC client. To learn how to create a client, visit: https://go.microsoft.com/fwlink/?linkid=2086909");

app.Run();
