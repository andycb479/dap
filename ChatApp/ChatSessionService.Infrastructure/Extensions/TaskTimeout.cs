namespace Services.Infrastructure.Extensions
{
     public static class TaskTimeout
     {
          public static async Task<TResult> TimeoutAfter<TResult>(this Task<TResult> task, int millisecondsTimeout)
          {
               if (task == await Task.WhenAny(task, Task.Delay(millisecondsTimeout)))
                    return await task;
               else
                    throw new TimeoutException();
          }
     }
}
