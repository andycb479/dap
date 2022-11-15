using MongoDB.Driver;

namespace Services.Infrastructure.Configurations;

public class MongoDbSettings : IMongoDbSettings
{
     public string DatabaseName { get; set; }
     public string ConnectionString { get; set; }
     public ReadPreferenceMode ReadPreferenceMode { get; set; }
}