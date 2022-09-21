using System.Reflection;
using ChatSessionService.Configuration;
using ChatSessionService.Infrastructure.Mapper;
using ChatSessionService.Services;
using Services.Core.ServiceDiscovery;

var builder = WebApplication.CreateBuilder(args);

// Additional configuration is required to successfully run gRPC on macOS.
// For instructions on how to configure Kestrel and gRPC clients on macOS, visit https://go.microsoft.com/fwlink/?linkid=2099682

// Add services to the container.
builder.Services.AddGrpc();
builder.Services.AddConsul(builder.Configuration.GetServiceConfig());

builder.Services.AddAutoMapper(Assembly.GetAssembly(typeof(MapperIndex)));

builder.Services.ConfigureDataLayer(builder.Configuration);
builder.Services.ConfigureBusinessLayer(builder.Configuration);

var app = builder.Build();

// Configure the HTTP request pipeline.
app.UseRouting();

app.UseEndpoints(endpoints =>
{
     endpoints.MapGrpcService<GreeterService>();
     endpoints.MapGrpcService<HealthCheckService>();
     endpoints.MapGrpcService<MessagesService>();

     endpoints.MapGet("/", async context =>
     {
          await context.Response.WriteAsync("Communication with gRPC endpoints must be made through a gRPC client. To learn how to create a client, visit: https://go.microsoft.com/fwlink/?linkid=2086909");
     });
});

app.Run();
