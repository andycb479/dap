using Users;

namespace ExternalServices.Services;

public interface IUsersService
{
     Task<User?> GetUserAsync(string clientIdentifier, int userId);

     Task DeleteUserAsync(string clientIdentifier, int userId, Guid transactionId);

     Task RollbackUserDeleteAsync(string clientIdentifier, int userId, Guid transactionId);
}