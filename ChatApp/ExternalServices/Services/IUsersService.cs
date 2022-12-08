using ExternalServices.Users;

namespace ExternalServices.Services;

public interface IUsersService
{
     Task<User?> GetUserAsync(int userId);

     Task DeleteUserAsync(int userId);

     Task RollbackUserDeleteAsync(int userId);
}