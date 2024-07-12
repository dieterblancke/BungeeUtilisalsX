package dev.endoy.bungeeutilisalsx.common.storage.sql;

import dev.endoy.bungeeutilisalsx.common.api.storage.AbstractStorageManager;
import dev.endoy.bungeeutilisalsx.common.api.storage.StorageType;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.Dao;

public abstract class SQLStorageManager extends AbstractStorageManager
{
    public SQLStorageManager( final StorageType type, final Dao dao )
    {
        super( type, dao );
    }
}
