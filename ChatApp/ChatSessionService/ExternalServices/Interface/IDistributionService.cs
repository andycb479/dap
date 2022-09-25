using Services.Infrastructure.Entity;

namespace ChatSessionService.ExternalServices.Interface;

public interface IDistributionService
{
    void RedirectMessage(MessageEntity message);
}