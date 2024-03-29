package be.dieterblancke.bungeeutilisalsx.common.migration;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.StorageType;
import be.dieterblancke.bungeeutilisalsx.common.migration.config.ConfigMigrationManager;
import be.dieterblancke.bungeeutilisalsx.common.migration.mongo.MongoMigrationManager;
import be.dieterblancke.bungeeutilisalsx.common.migration.sql.SqlMigrationManager;

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