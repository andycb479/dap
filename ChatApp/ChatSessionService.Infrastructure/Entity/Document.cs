using MongoDB.Bson;
using Newtonsoft.Json;
using Services.Infrastructure.Attributes;

namespace Services.Infrastructure.Entity;

public abstract class Document : IDocument
{
     [JsonConverter(typeof(ObjectIdConverter))]
     public ObjectId Id { get; set; }

     public DateTime CreatedAt => Id.CreationTime;
}