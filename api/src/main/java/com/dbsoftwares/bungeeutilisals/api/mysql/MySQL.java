package com.dbsoftwares.bungeeutilisals.api.mysql;

/*
 * Created by DBSoftwares on 04/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

public class MySQL {

    public static MySQLFinder singleFind(String table) {
        return new MySQLFinder(table);
    }

    public static MySQLMultiFinder multiFind(String table) {
        return new MySQLMultiFinder(table);
    }
}