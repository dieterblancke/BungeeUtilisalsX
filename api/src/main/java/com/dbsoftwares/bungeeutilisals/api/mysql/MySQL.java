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
import com.zaxxer.hikari.pool.ProxyConnection;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQL {

    public static <T> MySQLFinder<T> find(Class<T> table) {
        if (!table.isAnnotationPresent(StorageTable.class)) {
            return null;
        }
        return new MySQLFinder<>(table);
    }

    public static <T> T update(T table, String where, String... replacements) {
        if (!table.getClass().isAnnotationPresent(StorageTable.class)) {
            return table;
        }
        StorageTable storageTable = table.getClass().getDeclaredAnnotation(StorageTable.class);
        where = String.format(where, (Object[]) replacements);

        StringBuilder builder = new StringBuilder();
        builder.append("UPDATE ").append(PlaceHolderAPI.formatMessage(storageTable.name())).append(" SET ");
        for (Field field : table.getClass().getFields()) {
            if (!field.isAnnotationPresent(StorageColumn.class)) {
                continue;
            }
            StorageColumn column = field.getDeclaredAnnotation(StorageColumn.class);
            if (!column.updateable()) {
                continue;
            }
            field.setAccessible(true);
            try {
                String name = field.getName();
                Object value = field.get(table);

                if (value != null) {
                    if (value instanceof Number) {
                        builder.append(field.getName()).append(" = ").append(value).append(", ");
                    } else {
                        builder.append(field.getName()).append(" = ").append("'").append(value).append("'").append(", ");
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        builder.delete(builder.length() - 2, builder.length());
        builder.append(" WHERE ").append("'").append(where).append("'").append(";");

        try (ProxyConnection connection = BUCore.getApi().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(builder.toString());

            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return table;
    }

    public static <T> T insert(T table) {
        if (!table.getClass().isAnnotationPresent(StorageTable.class)) {
            return table;
        }
        StorageTable storageTable = table.getClass().getDeclaredAnnotation(StorageTable.class);
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
            field.setAccessible(true);
            try {
                Object value = field.get(table);

                columnBuilder.append(field.getName()).append(", ");
                if (value instanceof Number) {
                    valueBuilder.append(value == null ? "NULL" : value).append(", ");
                } else {
                    valueBuilder.append(value == null ? "NULL" : "'" + value + "'").append(", ");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        columnBuilder.delete(columnBuilder.length() - 2, columnBuilder.length());
        valueBuilder.delete(valueBuilder.length() - 2, valueBuilder.length());

        try (ProxyConnection connection = BUCore.getApi().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(String.format("INSERT INTO %s (%s) VALUES (%s);",
                    PlaceHolderAPI.formatMessage(storageTable.name()), columnBuilder.toString(), valueBuilder.toString()));

            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return table;
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
                field.setAccessible(true);
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