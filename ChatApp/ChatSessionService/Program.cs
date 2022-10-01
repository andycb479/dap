using System.Reflection;
using ChatSessionService.Configuration;
using ChatSessionService.Services;
using Services.Core.ServiceDiscovery;
using Services.Infrastructure.Mapper;

var builder = WebApplication.CreateBuilder(args);
builder.Logging.ClearProviders();
builder.Logging.AddConsole();

builder.Services.AddGrpc();
builder.Services.AddConsul(builder.Configuration.GetServiceConfig());

builder.Services.AddAutoMapper(Assembly.GetAssembly(typeof(MapperIndex)));

builder.Services.ConfigureDataLayer(builder.Configuration);
builder.Services.ConfigureBusinessLayer(builder.Configuration);
builder.Services.ConfigureRedisCache(builder.Configuration);

var app = builder.Build();

app.UseRouting();

app.UseEndpoints(endpoints =>
{
     endpoints.MapGrpcService<HealthCheckService>();
     endpoints.MapGrpcService<MessagesService>();

     endpoints.MapGet("/", async context =>
     {
          await context.Response.WriteAsync("Communication with gRPC endpoints must be made through a gRPC client. To learn how to create a client, visit: https://go.microsoft.com/fwlink/?linkid=2086909");
     });
});

app.Run();
