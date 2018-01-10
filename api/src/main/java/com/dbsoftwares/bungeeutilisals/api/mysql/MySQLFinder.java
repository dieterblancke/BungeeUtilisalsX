package com.dbsoftwares.bungeeutilisals.api.mysql;

/*
 * Created by DBSoftwares on 04/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 * May only be used for CentrixPVP
 */

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.mysql.storage.StorageColumn;
import com.dbsoftwares.bungeeutilisals.api.mysql.storage.StorageTable;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.utils.Validate;
import com.google.common.collect.Maps;
import com.zaxxer.hikari.pool.ProxyConnection;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class MySQLFinder<T> {

    private Class<T> table;
    private StorageTable storageTable;
    private String condition;
    private String column;

    public MySQLFinder(Class<T> table) {
        this.table = table;
        this.storageTable = table.getDeclaredAnnotation(StorageTable.class);
    }

    public MySQLFinder<T> where(String condition, Object... replacements) {
        for (int i = 0; i < replacements.length; i++) {
            if (!(replacements[i] instanceof Number)) {
                if (!replacements[i].toString().startsWith("'") && !replacements[i].toString().endsWith("'")) {
                    replacements[i] = "'" + replacements[i] + "'";
                }
            }
        }
        this.condition = String.format(condition, (Object[]) replacements);
        return this;
    }

    public MySQLFinder<T> select(String column, Object... replacements) {
        this.column = String.format(column, (Object[]) replacements);
        return this;
    }

    @SuppressWarnings("unchecked")
    public T find() {
        Validate.notNull(table, "Table cannot be null!");
        Validate.notNull(condition, "Condition cannot be null!");
        Validate.notNull(column, "Column cannot be null!");

        String statement = "SELECT " + column + " FROM " + PlaceHolderAPI.formatMessage(storageTable.name()) + " WHERE " + condition + ";";

        Object instance;
        try {
            instance = table.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }

        LinkedHashMap<String, Field> fields = Maps.newLinkedHashMap();
        for (Field field : table.getDeclaredFields()) {
            if (!field.isAnnotationPresent(StorageColumn.class)) {
                continue;
            }
            field.setAccessible(true);
            if (column.equalsIgnoreCase("*")) {
                fields.put(field.getName(), field);
            }
            for (String column : column.split(", ")) {
                if (field.getName().equalsIgnoreCase(column)) {
                    fields.put(column, field);
                }
            }
        }

        try (ProxyConnection connection = BUCore.getApi().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                for (Map.Entry<String, Field> entry : fields.entrySet()) {
                    String column = entry.getKey();
                    Field field = entry.getValue();

                    try {
                        field.set(instance, rs.getObject(column));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                return null;
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