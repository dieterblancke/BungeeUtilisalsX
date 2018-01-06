package com.dbsoftwares.bungeeutilisals.api.mysql;

/*
 * Created by DBSoftwares on 04/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 * May only be used for CentrixPVP
 */

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.mysql.storage.StorageTable;
import com.dbsoftwares.bungeeutilisals.api.utils.ReflectionUtils;
import com.dbsoftwares.bungeeutilisals.api.utils.Validate;
import com.google.common.collect.Maps;
import com.zaxxer.hikari.pool.ProxyConnection;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;

public class MySQLFinder<T> {

    private Class<T> table;
    private StorageTable storageTable;
    private String condition;
    private String column;

    public MySQLFinder(Class<T> table) {
        this.table = table;
        this.storageTable = table.getDeclaredAnnotation(StorageTable.class);
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
    public T find() {
        Validate.notNull(table, "Table cannot be null!");
        Validate.notNull(condition, "Condition cannot be null!");
        Validate.notNull(column, "Column cannot be null!");

        String statement = "SELECT " + column + " FROM " + storageTable.name() + " WHERE " + condition + ";";

        Object instance;
        try {
            instance = table.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }

        LinkedHashMap<String, Field> fields = Maps.newLinkedHashMap();
        for (String column : column.split(", ")) {
            fields.put(column, ReflectionUtils.getField(table, column));
        }

        try (ProxyConnection connection = BUCore.getApi().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            ResultSet rs = preparedStatement.executeQuery();

            MultiDataObject multiDataObject = new MultiDataObject();
            if (rs.next()) {
                for (String column : this.column.split(", ")) {
                    try {
                        fields.get(column).set(instance, rs.getObject(column));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

            rs.close();
            preparedStatement.close();
            connection.close();
            return (T) instance;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}