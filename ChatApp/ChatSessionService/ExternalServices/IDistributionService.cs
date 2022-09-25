namespace ChatSessionService.ExternalServices;

public interface IDistributionService
{
    Task RedirectMessage(Message message);
}