namespace ExternalServices.Services;

public interface IMessagesService
{
     Task DeleteUserMessages();

     Task RollbackDeleteUserMessages();
}