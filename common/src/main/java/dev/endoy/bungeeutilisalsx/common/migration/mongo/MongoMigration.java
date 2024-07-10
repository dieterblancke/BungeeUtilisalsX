package dev.endoy.bungeeutilisalsx.common.migration.mongo;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.migration.Migration;
import dev.endoy.bungeeutilisalsx.common.storage.mongodb.MongoDBStorageManager;
import com.mongodb.client.MongoDatabase;

public interface MongoMigration extends Migration
{

    default MongoDatabase db()
    {
        return ( (MongoDBStorageManager) BuX.getInstance().getAbstractStorageManager() ).getDatabase();
    }
}
