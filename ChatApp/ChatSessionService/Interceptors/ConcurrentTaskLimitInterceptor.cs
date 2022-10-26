using Grpc.Core;
using Grpc.Core.Interceptors;

namespace ChatSessionService.Interceptors
{
     public class ConcurrentTaskLimitInterceptor : Interceptor
     {
          private readonly ILogger<ConcurrentTaskLimitInterceptor> _logger;
          private SemaphoreSlim _concurrencySemaphore;
          private readonly int _maxConcurrentTasks;

          public ConcurrentTaskLimitInterceptor(ILogger<ConcurrentTaskLimitInterceptor> logger, IConfiguration configuration)
          {
               _logger = logger;
               _maxConcurrentTasks = configuration.GetValue<int>("ConcurrentTaskLimit:MaxConcurrentTasks");
               _concurrencySemaphore =
                    new SemaphoreSlim(_maxConcurrentTasks);
          }

          public override async Task<TResponse> UnaryServerHandler<TRequest, TResponse>(TRequest request,
               ServerCallContext context, UnaryServerMethod<TRequest, TResponse> continuation)
          {
               _logger.LogInformation($"Starting receiving call. Type: {MethodType.Unary}. " +
                                      $"Method: {context.Method}.");
               if (_concurrencySemaphore.CurrentCount == 0)
               {
                    throw new RpcException(new Status(StatusCode.Unavailable,
                         Newtonsoft.Json.JsonConvert.SerializeObject(new { Code = 429 })));
               }

               try
               {
                    await _concurrencySemaphore.WaitAsync();
                    return await continuation(request, context);
               }
               catch (Exception ex)
               {
                    _logger.LogError(ex, $"Error thrown by {context.Method}.");
                    throw;
               }
               finally
               {
                    _concurrencySemaphore.Release();
               }
          }

          public override async Task ServerStreamingServerHandler<TRequest, TResponse>(TRequest request,
               IServerStreamWriter<TResponse> responseStream, ServerCallContext context,
               ServerStreamingServerMethod<TRequest, TResponse> continuation)
          {
               _logger.LogInformation($"Starting receiving call. Type: {MethodType.ServerStreaming}. " +
                                      $"Method: {context.Method}.");

               if (_concurrencySemaphore.CurrentCount == _maxConcurrentTasks - 1)
               {
                    throw new RpcException(new Status(StatusCode.Unavailable,
                         Newtonsoft.Json.JsonConvert.SerializeObject(new { Code = 429 })));
               }

               try
               {
                    await _concurrencySemaphore.WaitAsync();
                    await continuation(request, responseStream, context);
               }
               catch (Exception ex)
               {
                    _logger.LogError(ex, $"Error thrown by {context.Method}.");
                    throw;
               }
               finally
               {
                    _concurrencySemaphore.Release();
               }
          }
     }
}
