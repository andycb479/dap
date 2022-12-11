using SagaOrchestrator.Configuration;
using SagaOrchestrator.Services;
using Serilog;
using Services.Core.ServiceDiscovery;

var builder = WebApplication.CreateBuilder(args);

builder.Host.UseSerilog((hostContext, services, configuration) =>
{
     configuration.WriteTo.Console();
});

builder.Services.AddConsul(builder.Configuration.GetServiceConfig());

// Add services to the container.
builder.Services.AddGrpc();

builder.Services.ConfigureServices(builder.Configuration);
builder.Services.ConfigureInfrastructure(builder.Configuration);
builder.Services.ConfigureRedisCache(builder.Configuration);

builder.Services.ConfigureSagas(builder.Configuration);

var app = builder.Build();

// Configure the HTTP request pipeline.
app.MapGrpcService<OrchestratorService>();
app.MapGrpcService<HealthCheckService>();
app.MapGet("/", () => "Communication with gRPC endpoints must be made through a gRPC client. To learn how to create a client, visit: https://go.microsoft.com/fwlink/?linkid=2086909");

app.Run();
