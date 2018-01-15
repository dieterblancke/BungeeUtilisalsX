package com.dbsoftwares.bungeeutilisals.bungee.storage.connection;

/*
 * Created by DBSoftwares on 15/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.mysql.storage.StorageTable;
import com.dbsoftwares.bungeeutilisals.bungee.storage.StorageType;
import com.dbsoftwares.bungeeutilisals.bungee.storage.exception.ConnectionException;
import com.google.common.collect.Maps;
import lombok.Data;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.LinkedHashMap;

@Data
public abstract class AbstractConnection {

    private StorageType type;

    public AbstractConnection(StorageType type) {
        this.type = type;
    }

    public abstract void createTable(Object table) throws ConnectionException;

    public abstract void executeInsert(Object table) throws ConnectionException;

    public abstract ResultSet executeQuery(String statement, String... replacements) throws ConnectionException;

    public abstract void executeUpdate(String statement, String replacements) throws ConnectionException;

    public LinkedHashMap<String, Object> readFields(Object table) {
        LinkedHashMap<String, Object> data = Maps.newLinkedHashMap();
        Class<?> clazz = table.getClass();

        if (!clazz.isAnnotationPresent(StorageTable.class)) {
            return data;
        }
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);

            try {
                Object value = field.get(table);

                if (value != null) {
                    data.put(field.getName(), value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return data;
    }
}