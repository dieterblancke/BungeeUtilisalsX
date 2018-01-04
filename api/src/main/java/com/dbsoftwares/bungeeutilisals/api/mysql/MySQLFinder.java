package com.dbsoftwares.bungeeutilisals.api.mysql;

/*
 * Created by DBSoftwares on 04/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.utils.Validate;
import com.zaxxer.hikari.pool.ProxyConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLFinder {

    private String table;
    private String condition;
    private String column;

    public MySQLFinder(String table) {
        this.table = table;
    }

    public MySQLFinder where(String condition, String... replacements) {
        this.condition = String.format(condition, (Object[]) replacements);
        return this;
    }

    public MySQLFinder select(String column, String... replacements) {
        this.column = String.format(condition, (Object[]) replacements);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T find() {
        Validate.notNull(table, "Table cannot be null!");
        Validate.notNull(condition, "Condition cannot be null!");
        Validate.notNull(column, "Column cannot be null!");

        String statement = "SELECT " + column + " FROM " + table + " WHERE " + condition + ";";

        try (ProxyConnection connection = BUCore.getApi().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            ResultSet rs = preparedStatement.executeQuery();

            T value = null;
            if (rs.next()) {
                value = (T) rs.getObject(column);
            }

            rs.close();
            preparedStatement.close();
            connection.close();
            return value;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}