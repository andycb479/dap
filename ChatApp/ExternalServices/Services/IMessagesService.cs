namespace ExternalServices.Services;

public interface IMessagesService
{
     Task DeleteUserMessages(string clientIdentifier, int userId);
}