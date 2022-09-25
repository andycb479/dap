using MongoDB.Bson;

namespace Services.Infrastructure.Entity;

public abstract class Document : IDocument
{
     public ObjectId Id { get; set; }

     public DateTime CreatedAt => Id.CreationTime;
}