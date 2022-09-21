using MongoDB.Bson;

namespace ChatSessionService.Infrastructure.Entity;

public abstract class Document : IDocument
{
     public ObjectId Id { get; set; }

     public DateTime CreatedAt => Id.CreationTime;
}