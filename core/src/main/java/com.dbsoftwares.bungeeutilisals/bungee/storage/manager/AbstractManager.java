package com.dbsoftwares.bungeeutilisals.bungee.storage.manager;

/*
 * Created by DBSoftwares on 15/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */


import lombok.Data;

import java.sql.Connection;
import java.sql.SQLException;

@Data
public abstract class AbstractManager {

    public abstract Connection getConnection() throws SQLException;

    public abstract void close();

}