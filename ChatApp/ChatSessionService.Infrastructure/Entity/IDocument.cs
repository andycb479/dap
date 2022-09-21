using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;

namespace ChatSessionService.Infrastructure.Entity
{
    public interface IDocument
    {
        [BsonId]
        [BsonRepresentation(BsonType.String)]
        ObjectId Id { get; set; }

        DateTime CreatedAt { get; }
    }
}
