using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ChatSessionService.Infrastructure.Configurations
{
     public interface IMongoDbSettings
     {
          string DatabaseName { get; set; }
          string ConnectionString { get; set; }
     }
}
