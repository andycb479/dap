namespace ExternalServices.Services;

public interface IUsersService
{
     Task<User?> GetUserAsync(int userId);
}