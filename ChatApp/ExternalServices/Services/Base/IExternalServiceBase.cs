namespace ExternalServices.Services.Base;

public interface IExternalServiceBase
{
     string CreateChatCacheKey<T>(string clientIdentifier, int userId);
}