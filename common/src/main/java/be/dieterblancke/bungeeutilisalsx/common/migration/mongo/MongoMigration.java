package be.dieterblancke.bungeeutilisalsx.common.migration.mongo;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.migration.Migration;
import be.dieterblancke.bungeeutilisalsx.common.storage.mongodb.MongoDBStorageManager;
import com.mongodb.client.MongoDatabase;

public interface MongoMigration extends Migration
{

    default MongoDatabase db()
    {
        return ( (MongoDBStorageManager) BuX.getInstance().getAbstractStorageManager() ).getDatabase();
    }
}
