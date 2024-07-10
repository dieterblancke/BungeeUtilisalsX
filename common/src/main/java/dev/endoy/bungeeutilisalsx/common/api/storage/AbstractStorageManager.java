package dev.endoy.bungeeutilisalsx.common.api.storage;

import dev.endoy.bungeeutilisalsx.common.api.storage.dao.Dao;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;

@Data
@RequiredArgsConstructor
public abstract class AbstractStorageManager
{

    private final StorageType type;
    private final Dao dao;

    public String getName()
    {
        return type.getName();
    }

    public abstract Connection getConnection() throws SQLException;

    public abstract void close() throws SQLException;

}