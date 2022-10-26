namespace Services.Infrastructure.Exceptions
{
     public class ServiceNotAvailableException : Exception
     {
          public ServiceNotAvailableException()
          {
          }

          public ServiceNotAvailableException(string serviceName)
               : base($"Service {serviceName} not available!")
          {

          }
     }
}
