using Services.Infrastructure.Attributes;
using Services.Infrastructure.Enums;

namespace Services.Infrastructure.Entity
{
     [BsonCollection("messages")]
     public class MessageEntity : Document
     {
          public int FromUserId  { get; set; }
          public int ToUserId  { get; set; }
          public string? MessageContent { get; set; }
          public MessageStatus MessageStatus { get; set; }
     }
}
