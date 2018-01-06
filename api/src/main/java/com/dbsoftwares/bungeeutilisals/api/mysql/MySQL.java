package com.dbsoftwares.bungeeutilisals.api.mysql;

/*
 * Created by DBSoftwares on 04/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */


import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.mysql.storage.StorageColumn;
import com.dbsoftwares.bungeeutilisals.api.mysql.storage.StorageTable;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.google.common.collect.Lists;
import com.zaxxer.hikari.pool.ProxyConnection;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;

public class MySQL {

    public static <T> MySQLFinder<T> find(Class<T> table) {
        if (!table.isAnnotationPresent(StorageTable.class)) {
            return null;
        }
        return new MySQLFinder<>(table);
    }

    public static <T> void insert(T table) {
        if (!table.getClass().isAnnotationPresent(StorageTable.class)) {
            return;
        }
        StorageTable storageTable = table.getClass().getDeclaredAnnotation(StorageTable.class);
        LinkedList<Field> fields = Lists.newLinkedList();
        StringBuilder builder = new StringBuilder();

        StringBuilder columnBuilder = new StringBuilder();
        StringBuilder valueBuilder = new StringBuilder();

        for (Field field : table.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(StorageColumn.class)) {
                continue;
            }
            StorageColumn column = field.getDeclaredAnnotation(StorageColumn.class);
            if (column.autoincrement()) {
                continue;
            }
            try {
                Object value = field.get(table);

                columnBuilder.append(field.getName()).append(", ");
                valueBuilder.append(value == null ? "NULL" : value).append(", ");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        columnBuilder.delete(columnBuilder.length() - 2, columnBuilder.length());
        valueBuilder.delete(valueBuilder.length() - 2, valueBuilder.length());

        try (ProxyConnection connection = BUCore.getApi().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(String.format("INSERT INTO " + storageTable.name() +
                    " (%s) VALUES (%s);", columnBuilder.toString(), valueBuilder.toString()));

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createDefaultTables(Class... tables) {
        for (Class<?> table : tables) {
            if (!table.isAnnotationPresent(StorageTable.class)) {
                continue;
            }
            StorageTable storageTable = table.getDeclaredAnnotation(StorageTable.class);

            StringBuilder builder = new StringBuilder();

            builder.append("CREATE TABLE IF NOT EXISTS ");
            builder.append(PlaceHolderAPI.formatMessage(storageTable.name()));
            builder.append(" (");

            for (Field field : table.getDeclaredFields()) {
                if (!field.isAnnotationPresent(StorageColumn.class)) {
                    continue;
                }
                String name = field.getName();
                StorageColumn storageColumn = field.getDeclaredAnnotation(StorageColumn.class);

                builder.append(name);
                builder.append(" ").append(storageColumn.type());

                if (!storageColumn.nullable()) {
                    builder.append(" NOT NULL");
                }
                if (storageColumn.primary()) {
                    builder.append(" PRIMARY KEY");
                }
                if (storageColumn.unique()) {
                    builder.append(" UNIQUE");
                }
                if (storageColumn.autoincrement()) {
                    builder.append(" AUTO_INCREMENT");
                }
                if (!storageColumn.def().equals("")) {
                    builder.append(" DEFAULT ").append(storageColumn.def());
                }

                builder.append(", ");
            }
            if (storageTable.primary().length > 0) {
                String primary = "PRIMARY KEY (";

                for (String key : storageTable.primary()) {
                    primary = primary + key + ", ";
                }

                primary = primary.substring(0, primary.length() - 2) + "), ";
                builder.append(primary);
            }
            if (storageTable.indexes().length > 0) {
                for (String index : storageTable.indexes()) {
                    builder.append("KEY idx_").append(index).append(" (").append(index).append("), ");
                }
            }
            if (storageTable.foreign().length > 0) {
                for (String foreign : storageTable.foreign()) {
                    builder.append("FOREIGN KEY ").append("(").append(foreign.split(" => ")[0]).append(")")
                            .append(" REFERENCES ").append(PlaceHolderAPI.formatMessage(foreign.split(" => ")[1])).append(", ");
                }
            }

            builder.delete(builder.length() - 2, builder.length());

            builder.append(") ENGINE=");
            builder.append(storageTable.engine());
            builder.append(" AUTO_INCREMENT=");
            builder.append(storageTable.autoincrement());
            builder.append(" DEFAULT CHARSET=");
            builder.append(storageTable.charset());
            builder.append(";");

            try (ProxyConnection connection = BUCore.getApi().getConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement(builder.toString());

                preparedStatement.executeUpdate();
                preparedStatement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    void test() {
        MultiDataObject data = find("bu_users").select("username, language")
                .where("username = %s", "didjee2").find();

        String username = data.getCastedValue("username");
        String language = data.getCastedValue("language");
        // Do stuff
    }*/
}