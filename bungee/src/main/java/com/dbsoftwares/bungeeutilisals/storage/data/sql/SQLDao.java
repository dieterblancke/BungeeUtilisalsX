package com.dbsoftwares.bungeeutilisals.storage.data.sql;

import com.dbsoftwares.bungeeutilisals.api.storage.dao.Dao;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.PunishmentDao;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.UserDao;
import com.dbsoftwares.bungeeutilisals.storage.data.sql.dao.SQLPunishmentDao;
import com.dbsoftwares.bungeeutilisals.storage.data.sql.dao.SQLUserDao;

/*
 * Created by DBSoftwares on 10 augustus 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */
public class SQLDao implements Dao {

    private UserDao userDao;
    private PunishmentDao punishmentDao;

    public SQLDao() {
        this.userDao = new SQLUserDao();
        this.punishmentDao = new SQLPunishmentDao();
    }

    @Override
    public UserDao getUserDao() {
        return userDao;
    }

    @Override
    public PunishmentDao getPunishmentDao() {
        return punishmentDao;
    }
}
