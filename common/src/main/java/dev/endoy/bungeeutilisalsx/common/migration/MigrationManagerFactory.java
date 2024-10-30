package dev.endoy.bungeeutilisalsx.common.migration;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.storage.StorageType;
import dev.endoy.bungeeutilisalsx.common.migration.config.ConfigMigrationManager;
import dev.endoy.bungeeutilisalsx.common.migration.mongo.MongoMigrationManager;
import dev.endoy.bungeeutilisalsx.common.migration.sql.SqlMigrationManager;

public class MigrationManagerFactory
{

    private MigrationManagerFactory()
    {
    }

    public static MigrationManager createMigrationManager()
    {
        final StorageType type = BuX.getInstance().getAbstractStorageManager().getType();

        if ( type == StorageType.MONGODB )
        {
            return new MongoMigrationManager();
        }
        return new SqlMigrationManager();
    }

    public static MigrationManager createConfigMigrationManager()
    {
        return new ConfigMigrationManager();
    }
}