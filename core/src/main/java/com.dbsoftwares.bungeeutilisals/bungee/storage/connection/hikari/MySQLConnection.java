package com.dbsoftwares.bungeeutilisals.bungee.storage.connection.hikari;

/*
 * Created by DBSoftwares on 15/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.storage.AbstractConnection;
import com.dbsoftwares.bungeeutilisals.api.storage.exception.ConnectionException;

import java.sql.*;

public class MySQLConnection extends AbstractConnection {

    public MySQLConnection(Connection connection) {
        super(connection);
    }

    @Override
    public ResultSet executeQuery(String statement, String... replacements) throws ConnectionException {
        try {
            return connection.createStatement().executeQuery(String.format(statement, (Object[]) replacements));
        } catch (SQLException e) {
            throw new ConnectionException("An error occured while creating a table!", e);
        }
    }

    @Override
    public void executeUpdate(String statement, String... replacements) throws ConnectionException {
        try {
            connection.createStatement().executeUpdate(String.format(statement, (Object[]) replacements));
        } catch (SQLException e) {
            throw new ConnectionException("An error occured while creating a table!", e);
        }
    }

    @Override
    public PreparedStatement prepareStatement(String statement) throws ConnectionException {
        try {
            return connection.prepareStatement(statement);
        } catch (SQLException e) {
            throw new ConnectionException("Could not prepare statement!", e);
        }
    }

    @Override
    public Statement createStatement() throws ConnectionException {
        try {
            return connection.createStatement();
        } catch (SQLException e) {
            throw new ConnectionException("Could not create statement!", e);
        }
    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        super.close();
    }
}