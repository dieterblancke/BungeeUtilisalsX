package be.dieterblancke.bungeeutilisalsx.common.migration;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.StorageType;
import be.dieterblancke.bungeeutilisalsx.common.migration.sql.SqlMigrationManager;

public class MigrationManagerFactory
{

    public static MigrationManager createMigrationManager() {
        final StorageType type = BuX.getApi().getStorageManager().getType();

        if (type == StorageType.MONGODB) {
            return new MongoMigrationManager();
        }
        return new SqlMigrationManager();
    }

}