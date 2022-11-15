using System.Reflection;
using ChatSessionService.Configuration;
using ChatSessionService.Interceptors;
using ChatSessionService.Services;
using Serilog;
using Services.Core.ServiceDiscovery;
using Services.Infrastructure.Mapper;

var builder = WebApplication.CreateBuilder(args);

builder.Host.UseSerilog((hostContext, services, configuration) => {
     configuration.WriteTo.Console();
     configuration.Enrich.FromLogContext();
     configuration.WriteTo.Http("http://localhost:5044", null);
});

builder.Services.AddGrpc(options=>options.Interceptors.Add<ConcurrentTaskLimitInterceptor>());
builder.Services.AddConsul(builder.Configuration.GetServiceConfig());

builder.Services.AddAutoMapper(Assembly.GetAssembly(typeof(MapperIndex)));

builder.Services.ConfigureDataLayer(builder.Configuration);
builder.Services.ConfigureBusinessLayer(builder.Configuration);
builder.Services.ConfigureRedisCache(builder.Configuration);
builder.Services.ConfigureInfrastructure(builder.Configuration);

builder.Services.AddSingleton<ConcurrentTaskLimitInterceptor>();

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
