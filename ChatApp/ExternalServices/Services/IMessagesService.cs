namespace ExternalServices.Services;

public interface IMessagesService
{
     Task DeleteUserMessages(int userId);

     Task RollbackDeleteUserMessages(int userId);
}