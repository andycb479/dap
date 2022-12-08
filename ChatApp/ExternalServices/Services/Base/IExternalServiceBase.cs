namespace ExternalServices.Services.Base;

public interface IExternalServiceBase
{
     string CreateChatCacheKey<T>(int userId);
}