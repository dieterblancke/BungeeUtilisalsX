package com.dbsoftwares.bungeeutilisals.api.storage;

/*
 * Created by DBSoftwares on 15/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.storage.exception.ConnectionException;
import com.dbsoftwares.bungeeutilisals.api.utils.Validate;
import lombok.Data;

import java.sql.*;

@Data
public abstract class AbstractConnection implements AutoCloseable {

    // Class made to, in the future, support MongoDB.

    protected Connection connection;

    public AbstractConnection(Connection connection) {
        this.connection = connection;
    }

    public abstract ResultSet executeQuery(String statement, String... replacements) throws ConnectionException;

    public abstract void executeUpdate(String statement, String... replacements) throws ConnectionException;

    public void close() {
        Validate.ifNotNull(connection, (connection) -> {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public abstract PreparedStatement prepareStatement(String statement) throws ConnectionException;

    public abstract Statement createStatement() throws ConnectionException;
}