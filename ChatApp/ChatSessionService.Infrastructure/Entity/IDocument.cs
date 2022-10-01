using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;
using Newtonsoft.Json;
using Services.Infrastructure.Attributes;

namespace Services.Infrastructure.Entity
{
     public interface IDocument
     {
          [BsonId]
          [BsonRepresentation(BsonType.ObjectId)]
          [JsonConverter(typeof(ObjectIdConverter))]
          ObjectId Id { get; set; }

          DateTime CreatedAt { get; }
     }
}
