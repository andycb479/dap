using ExternalServices.Services;
using OpenSleigh.Core;
using OpenSleigh.Core.Messaging;

namespace SagaOrchestrator.Sagas
{
     public record DeleteUserSagaState : SagaState
     {
          public DeleteUserSagaState(Guid id) : base(id) { }
          public enum Steps
          {
               Processing,
               Successful,
               Failed
          };
          public Steps CurrentStatus { get; set; } = Steps.Processing;
          public int UserId { get; set; }
          public bool UsersServiceDeleteCompleted { get; set; }
          public bool ChatsServiceDeleteCompleted { get; set; }
     }

     public record DeleteUsersServiceUser(Guid Id, Guid CorrelationId, int UserId) : ICommand { }

     public record DeleteUserSagaCompleted(Guid Id, Guid CorrelationId) : IEvent { }
     public record UsersServiceDeleteCompleted(Guid Id, Guid CorrelationId) : IEvent { }

     public class DeleteUserSaga :
         Saga<DeleteUserSagaState>,
         IStartedBy<DeleteUsersServiceUser>,
         ICompensateMessage<DeleteUsersServiceUser>,
         IHandleMessage<UsersServiceDeleteCompleted>,
         ICompensateMessage<UsersServiceDeleteCompleted>,
         IHandleMessage<DeleteUserSagaCompleted>
     {
          private readonly ILogger<DeleteUserSaga> _logger;
          private readonly IMessagesService _messagesService;
          private readonly IUsersService _usersService;
          private readonly string _clientIdentifier;

          public DeleteUserSaga(ILogger<DeleteUserSaga> logger, IMessagesService messagesService,
               IUsersService usersService, DeleteUserSagaState state, IConfiguration configuration) : base(state)
          {
               _logger = logger ?? throw new ArgumentNullException(nameof(logger));
               _messagesService = messagesService;
               _usersService = usersService;
               _clientIdentifier = configuration.GetValue<string>("ClientIdentifier");
          }

          public async Task HandleAsync(IMessageContext<DeleteUsersServiceUser> context, CancellationToken cancellationToken = default)
          {
               _logger.LogInformation($"Starting Saga {context.Message.CorrelationId}");
               _logger.LogInformation($"Starting deletion of userId:{State.UserId} from Users Service.");

               State.UserId = context.Message.UserId;

               await _usersService.DeleteUserAsync(_clientIdentifier, context.Message.UserId, State.Id);

               State.UsersServiceDeleteCompleted = true;
               State.CurrentStatus = DeleteUserSagaState.Steps.Successful;

               _logger.LogInformation($"UserId:{State.UserId} deleted from Users Service.");

               var message = new UsersServiceDeleteCompleted(Guid.NewGuid(), context.Message.CorrelationId);
               Publish(message);
          }

          public async Task CompensateAsync(ICompensationContext<DeleteUsersServiceUser> context, CancellationToken cancellationToken = default)
          {
               _logger.LogWarning($"Deletion of userId:{State.UserId} from Users Service failed! Reason: {context.Exception.Message}.");

               State.CurrentStatus = DeleteUserSagaState.Steps.Failed;

               var message = new DeleteUserSagaCompleted(Guid.NewGuid(), context.MessageContext.Message.CorrelationId);
               Publish(message);
          }

          public async Task HandleAsync(IMessageContext<UsersServiceDeleteCompleted> context, CancellationToken cancellationToken = new CancellationToken())
          {
               _logger.LogInformation($"Starting deletion of messages from Chat Session Service for userId:{State.UserId}.");

               await _messagesService.DeleteUserMessages(_clientIdentifier, State.UserId);

               State.ChatsServiceDeleteCompleted = true;
               State.CurrentStatus = DeleteUserSagaState.Steps.Successful;
               _logger.LogInformation($"UserId:{State.UserId} messages deleted from Chat Session Service.");

               var message = new DeleteUserSagaCompleted(Guid.NewGuid(), context.Message.CorrelationId);
               Publish(message);
          }

          public async Task CompensateAsync(ICompensationContext<UsersServiceDeleteCompleted> context, CancellationToken cancellationToken = new CancellationToken())
          {
               _logger.LogWarning($"Deletion of messages from Chat Session Service failed! Reason: {context.Exception.Message}.");

               _logger.LogInformation($"Starting rollback of the deleted userId:{State.UserId}.");
               State.CurrentStatus = DeleteUserSagaState.Steps.Failed;

               await _usersService.RollbackUserDeleteAsync(_clientIdentifier, State.UserId, State.Id);

               _logger.LogInformation($"Deletion of the userId:{State.UserId} was rolled back.");

               var message = new DeleteUserSagaCompleted(Guid.NewGuid(), context.MessageContext.Message.CorrelationId);
               Publish(message);
          }

          public Task HandleAsync(IMessageContext<DeleteUserSagaCompleted> context, CancellationToken cancellationToken = default)
          {
               State.MarkAsCompleted();

               var isFailed = State.CurrentStatus == DeleteUserSagaState.Steps.Failed;
               if (isFailed)
                    _logger.LogWarning($"Saga - '{context.Message.CorrelationId}' - Failed!");
               else
                    _logger.LogInformation($"Saga - '{context.Message.CorrelationId}' - Completed!");

               return Task.CompletedTask;
          }
     }
}
