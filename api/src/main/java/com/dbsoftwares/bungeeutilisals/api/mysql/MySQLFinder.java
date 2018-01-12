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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zaxxer.hikari.pool.ProxyConnection;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Consumer;

public class MySQLFinder<T> {

    private Class<T> table;
    private StorageTable storageTable;
    private String condition;
    private String column;

    private LinkedList<T> tableInstances = Lists.newLinkedList();

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
    public MySQLFinder<T> search() {
        Validate.notNull(table, "Table cannot be null!");
        Validate.notNull(condition, "Condition cannot be null!");
        Validate.notNull(column, "Column cannot be null!");

        String statement = "SELECT " + column + " FROM " + PlaceHolderAPI.formatMessage(storageTable.name()) + " WHERE " + condition + ";";

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

            while (rs.next()) {
                try {
                    Object instance = table.newInstance();

                    for (Map.Entry<String, Field> entry : fields.entrySet()) {
                        String column = entry.getKey();
                        Field field = entry.getValue();

                        field.set(instance, rs.getObject(column));
                    }
                    tableInstances.add((T) instance);
                } catch (IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                }
            }

            rs.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    public T get() {
        if (tableInstances.isEmpty()) {
            return null;
        }
        return tableInstances.get(0);
    }

    public LinkedList<T> multiGet() {
        return tableInstances;
    }

    public boolean isPresent() {
        return !tableInstances.isEmpty();
    }

    public void ifPresent(Consumer<? super T> consumer) {
        if (!tableInstances.isEmpty()) {
            consumer.accept(get());
        }
    }

    public void ifMultiPresent(Consumer<LinkedList<? super T>> consumer) {
        if (!tableInstances.isEmpty()) {
            consumer.accept(tableInstances);
        }
    }
}