namespace ExternalServices.Services;

public interface IUsersService
{
     Task<User> GetUser(int userId);
}