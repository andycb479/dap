using ChatSessionService.Infrastructure.Attributes;
using ChatSessionService.Infrastructure.Enums;

namespace ChatSessionService.Infrastructure.Entity
{
     [BsonCollection("messages")]
     public class Message : Document
     {
          public int FromUserId  { get; set; }
          public int ToUserId  { get; set; }
          public string MessageContent { get; set; }
          public MessageStatus MessageStatus { get; set; }
     }
}
