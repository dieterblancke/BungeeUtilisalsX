package com.dbsoftwares.bungeeutilisals.bungee.storage.manager.file;

/*
 * Created by DBSoftwares on 15/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.bungee.storage.StorageType;
import com.dbsoftwares.bungeeutilisals.bungee.storage.manager.AbstractManager;

public class SQLiteManager extends AbstractManager {

    public SQLiteManager() {
        super(StorageType.SQLITE);
    }
}