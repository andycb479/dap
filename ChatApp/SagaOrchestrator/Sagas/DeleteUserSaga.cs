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

          public DeleteUserSaga(ILogger<DeleteUserSaga> logger, IMessagesService messagesService,
               IUsersService usersService, DeleteUserSagaState state) : base(state)
          {
               _logger = logger ?? throw new ArgumentNullException(nameof(logger));
               _messagesService = messagesService;
               _usersService = usersService;
          }

          public async Task HandleAsync(IMessageContext<DeleteUsersServiceUser> context, CancellationToken cancellationToken = default)
          {
               _logger.LogInformation($"Starting Saga {context.Message.CorrelationId}");
               _logger.LogInformation("Starting deletion of user from Users Service.");

               State.UserId = context.Message.UserId;

               await _usersService.DeleteUserAsync(context.Message.UserId);

               State.UsersServiceDeleteCompleted = true;
               State.CurrentStatus = DeleteUserSagaState.Steps.Successful;

               var message = new UsersServiceDeleteCompleted(Guid.NewGuid(), context.Message.CorrelationId);
               Publish(message);
          }

          public async Task CompensateAsync(ICompensationContext<DeleteUsersServiceUser> context, CancellationToken cancellationToken = default)
          {
               _logger.LogWarning($"Deletion of user from Users Service failed! Reason: {context.Exception.Message}");

               State.CurrentStatus = DeleteUserSagaState.Steps.Failed;

               await _usersService.RollbackUserDeleteAsync(State.UserId);

               var message = new DeleteUserSagaCompleted(Guid.NewGuid(), context.MessageContext.Message.CorrelationId);
               Publish(message);
          }

          public async Task HandleAsync(IMessageContext<UsersServiceDeleteCompleted> context, CancellationToken cancellationToken = new CancellationToken())
          {
               _logger.LogInformation("Starting deletion of messages from Chat Session Service.");

               await _messagesService.DeleteUserMessages(State.UserId);

               State.ChatsServiceDeleteCompleted = true;
               State.CurrentStatus = DeleteUserSagaState.Steps.Successful;

               var message = new DeleteUserSagaCompleted(Guid.NewGuid(), context.Message.CorrelationId);
               Publish(message);
          }

          public async Task CompensateAsync(ICompensationContext<UsersServiceDeleteCompleted> context, CancellationToken cancellationToken = new CancellationToken())
          {
               _logger.LogWarning($"Deletion of messages from Chat Session Service! Reason: {context.Exception.Message}");

               State.CurrentStatus = DeleteUserSagaState.Steps.Failed;

               await _messagesService.RollbackDeleteUserMessages(State.UserId);

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
