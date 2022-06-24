package be.dieterblancke.bungeeutilisalsx.common.storage.sql;

import be.dieterblancke.bungeeutilisalsx.common.api.storage.AbstractStorageManager;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.StorageType;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.Dao;

public abstract class SQLStorageManager extends AbstractStorageManager
{
    public SQLStorageManager( final StorageType type, final Dao dao )
    {
        super( type, dao );
    }
}
