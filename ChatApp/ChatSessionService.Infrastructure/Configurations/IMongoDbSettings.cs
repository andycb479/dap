namespace Services.Infrastructure.Configurations
{
     public interface IMongoDbSettings
     {
          string DatabaseName { get; set; }
          string ConnectionString { get; set; }
     }
}
